package ru.hits.attackdefenceplatform.modules.repo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.configuration.properties.CheckersProperties;
import ru.hits.attackdefenceplatform.modules.checker.CheckerManagementService;
import ru.hits.attackdefenceplatform.modules.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.modules.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.modules.checker.script.CheckerFileServiceNew;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.modules.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.modules.repo.model.RepositoryInfoDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepositorySyncService {

    private final RepositoryAdapter repositoryAdapter;
    private final RepositoryService repositoryService;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final CheckerRepository checkerRepository;

    private final CheckerFileServiceNew checkerFileServiceNew;

    /**
     * Основная точка синхронизации репозитория платформы.
     */
    @Transactional
    public void syncRepo() {
        RepositoryInfoDto repoInfo = repositoryService.getCurrentRepositoryFromDB();
        log.info("Начата синхронизация репозитория: {}", repoInfo.fullName());

        try {
            var repoData = repositoryAdapter.getRepositoryInfo(repoInfo.fullName());

            if (Objects.equals(repoInfo.lastCommitSha(), repoData.lastCommitSha())) {
                log.info("Синхронизация не требуется — коммиты совпадают ({}).", repoData.lastCommitSha());
                return;
            }

            log.info("Обнаружен новый коммит: {} (было {}). Начинаем синхронизацию...",
                    repoData.lastCommitSha(), repoInfo.lastCommitSha());

            // Загружаем список всех файлов из репозитория
            List<RepoFileDto> files = repositoryAdapter.getRepositoryTree(repoInfo.fullName(), repoInfo.defaultBranch());
            log.info("Загружено {} файлов из {}", files.size(), repoInfo.fullName());

            // Определяем сервисы и чекеры
            Map<String, VulnerableServiceEntity> detectedServices = detectServices(files, repoInfo.htmlUrl());
            Map<String, List<RepoFileDto>> detectedCheckers = detectCheckerFiles(files, detectedServices);

            // Синхронизируем с БД
            syncServices(detectedServices);
            syncCheckers(detectedCheckers, repoInfo);

            log.info("Синхронизация завершена. Найдено {} сервисов и {} чекеров.",
                    detectedServices.size(), detectedCheckers.size());

            // Обновляем commit SHA
            repositoryService.updateLastCommit(repoData.lastCommitSha());

        } catch (Exception e) {
            log.error("Ошибка при синхронизации {}: {}", repoInfo.fullName(), e.getMessage(), e);
            throw new IllegalStateException("Ошибка при синхронизации репозитория", e);
        }
    }

    // -------------------------------------------------------------------------
    // СЕРВИСЫ
    // -------------------------------------------------------------------------

    /**
     * Поиск сервисов по структуре services/<serviceName>/docker-compose.yml
     */
    private Map<String, VulnerableServiceEntity> detectServices(List<RepoFileDto> files, String repoUrl) {
        return files.stream()
                .filter(f -> f.getPath().startsWith("services/"))
                .filter(f -> f.getName().equalsIgnoreCase("docker-compose.yaml")
                        || f.getName().equalsIgnoreCase("docker-compose.yml"))
                .map(f -> {
                    String[] parts = f.getPath().split("/");
                    if (parts.length < 2) return null;

                    String serviceName = parts[1];

                    VulnerableServiceEntity service = new VulnerableServiceEntity();
                    service.setName(serviceName);
                    service.setGitRepositoryUrl(repoUrl);

                    // Порт можно будет позже вычислять лучше
                    service.setPort(8000 + new Random().nextInt(1000));

                    return service;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(VulnerableServiceEntity::getName, s -> s, (a, b) -> a));
    }


    /**
     * Синхронизация списка сервисов в базе
     */
    private void syncServices(Map<String, VulnerableServiceEntity> detected) {
        List<VulnerableServiceEntity> existing = vulnerableServiceRepository.findAll();

        // добавить новые
        for (VulnerableServiceEntity s : detected.values()) {
            boolean exists = existing.stream().anyMatch(e -> e.getName().equals(s.getName()));
            if (!exists) {
                vulnerableServiceRepository.save(s);
                log.info("Добавлен новый сервис: {}", s.getName());
            }
        }

        // удалить отсутствующие
        for (VulnerableServiceEntity s : existing) {
            if (!detected.containsKey(s.getName())) {
                vulnerableServiceRepository.delete(s);
                log.info("Удален сервис: {}", s.getName());
            }
        }
    }


    // -------------------------------------------------------------------------
    // ЧЕКЕРЫ
    // -------------------------------------------------------------------------

    /**
     * Определение файлов чекеров по структуре checkers/<serviceName>/
     */
    private Map<String, List<RepoFileDto>> detectCheckerFiles(
            List<RepoFileDto> allFiles,
            Map<String, VulnerableServiceEntity> services
    ) {

        Map<String, List<RepoFileDto>> grouped = allFiles.stream()
                .filter(f -> f.getPath().startsWith("checkers/"))
                .filter(f -> f.getType().equalsIgnoreCase("blob"))
                .collect(Collectors.groupingBy(f -> f.getPath().split("/")[1]));

        Map<String, List<RepoFileDto>> result = new HashMap<>();

        for (var entry : grouped.entrySet()) {
            String serviceName = entry.getKey();
            List<RepoFileDto> checkerFiles = entry.getValue();

            if (!services.containsKey(serviceName))
                continue;

            boolean hasRun = checkerFiles.stream()
                    .anyMatch(f -> f.getPath().equals("checkers/" + serviceName + "/run.py"));

            if (!hasRun) {
                log.warn("Чекер '{}' пропущен — отсутствует run.py", serviceName);
                continue;
            }

            result.put(serviceName, checkerFiles);
        }

        return result;
    }


    /**
     * Синхронизация чекеров: скачивание → сохранение файлов → обновление БД
     */
    private void syncCheckers(
            Map<String, List<RepoFileDto>> detected,
            RepositoryInfoDto repoInfo
    ) {
        // Загружаем *актуальный* список чекеров → key = serviceName(lowercase)
        Map<String, CheckerEntity> existingByService = checkerRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.getVulnerableService().getName().toLowerCase(),
                        c -> c
                ));

        // Какие чекеры мы обработали
        Set<String> processedServiceNames = new HashSet<>();

        for (var entry : detected.entrySet()) {

            String serviceName = entry.getKey().toLowerCase();
            List<RepoFileDto> checkerFiles = entry.getValue();

            // Находим сервис
            VulnerableServiceEntity service = vulnerableServiceRepository.findByName(serviceName)
                    .orElseThrow(() -> new IllegalStateException("Service not found: " + serviceName));

            // Создаем временную директорию
            Path tempDir;
            try {
                tempDir = Files.createTempDirectory("checker_sync_");
            } catch (IOException e) {
                throw new IllegalStateException("Cannot create temp directory", e);
            }

            // Скачиваем все файлы чекера
            for (RepoFileDto file : checkerFiles) {
                byte[] bytes = repositoryAdapter.getFileContent(
                        repoInfo.fullName(),
                        file.getPath(),
                        repoInfo.defaultBranch()
                );

                // checkers/<service>/<path> → <path>
                Path relative = Paths.get(file.getPath()).subpath(2, Paths.get(file.getPath()).getNameCount());
                Path out = tempDir.resolve(relative);

                try {
                    Files.createDirectories(out.getParent());
                    Files.write(out, bytes);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to download checker file " + file.getPath(), e);
                }
            }

            // Сохраняем в постоянное хранилище
            Path storedRoot;
            try {
                storedRoot = checkerFileServiceNew.saveCheckerDirectory(tempDir, serviceName);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot store checker files", e);
            }

            // Обновляем или создаём чекер
            CheckerEntity checker = existingByService.get(serviceName);
            if (checker == null) {
                checker = new CheckerEntity();
                checker.setVulnerableService(service);
            }

            checker.setScriptFilePath(storedRoot.resolve("run.py").toString());

            // Сохраняем (вне старого списка)
            checkerRepository.save(checker);

            processedServiceNames.add(serviceName);

            log.info("Чекер '{}' синхронизирован → {}", serviceName, storedRoot);
        }

        // Теперь снова загружаем *актуальный список* чекеров
        List<CheckerEntity> freshList = checkerRepository.findAll();

        // И удаляем только те, кто реально отсутствует
        for (CheckerEntity c : freshList) {
            String name = c.getVulnerableService().getName().toLowerCase();
            if (!processedServiceNames.contains(name)) {
                checkerRepository.delete(c);
                log.info("Удалён чекер: {}", c.getVulnerableService().getName());
            }
        }
    }
}



