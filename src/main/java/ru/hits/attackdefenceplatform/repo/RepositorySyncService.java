package ru.hits.attackdefenceplatform.repo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.repo.model.RepositoryInfoDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepositorySyncService {

    private final RepositoryAdapter repositoryAdapter;
    private final RepositoryService repositoryService;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final CheckerRepository checkerRepository;

    /**
     * Синхронизирует список сервисов и чекеров с текущим репозиторием платформы.
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


            // Получаем список файлов из GitHub
            List<RepoFileDto> files = repositoryAdapter.getRepositoryTree(repoInfo.fullName(), repoInfo.defaultBranch());
            log.info("Загружено {} файлов из репозитория {}", files.size(), repoInfo.fullName());

            // Определяем сервисы и чекеры
            Map<String, VulnerableServiceEntity> detectedServices = detectServices(files, repoInfo.htmlUrl());
            Map<String, CheckerEntity> detectedCheckers = detectCheckers(files, detectedServices);

            // Синхронизируем с БД
            syncServices(detectedServices);
            syncCheckers(detectedCheckers);

            log.info("Синхронизация завершена. Найдено {} сервисов и {} чекеров.",
                    detectedServices.size(), detectedCheckers.size());

        } catch (Exception e) {
            log.error("Ошибка при синхронизации {}: {}", repoInfo.fullName(), e.getMessage(), e);
            throw new IllegalStateException("Ошибка при синхронизации репозитория", e);
        }
    }

    /**
     * Поиск сервисов по структуре папок `services/*/
    private Map<String, VulnerableServiceEntity> detectServices(List<RepoFileDto> files, String repoUrl) {
        return files.stream()
                .filter(f -> f.getPath().startsWith("services/"))
                .filter(f -> f.getName().equalsIgnoreCase("docker-compose.yaml") || f.getName().equalsIgnoreCase("docker-compose.yml"))
                .map(f -> {
                    String[] parts = f.getPath().split("/");
                    if (parts.length < 2) return null;
                    String serviceName = parts[1];

                    VulnerableServiceEntity service = new VulnerableServiceEntity();
                    service.setName(serviceName);
                    service.setGitRepositoryUrl(repoUrl);
                    service.setPort(8000 + new Random().nextInt(1000));
                    return service;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(VulnerableServiceEntity::getName, s -> s, (a, b) -> a));
    }

    /**
     * Поиск чекеров по структуре `checkers/<serviceName>/*.py`
     */
    private Map<String, CheckerEntity> detectCheckers(
            List<RepoFileDto> files,
            Map<String, VulnerableServiceEntity> services
    ) {
        return files.stream()
                .filter(f -> f.getPath().startsWith("checkers/"))
                .filter(f -> f.getName().endsWith(".py"))
                .map(f -> {
                    String[] parts = f.getPath().split("/");
                    if (parts.length < 3) return null;

                    String serviceName = parts[1];
                    VulnerableServiceEntity service = services.get(serviceName);
                    if (service == null) return null;

                    CheckerEntity checker = new CheckerEntity();
                    checker.setVulnerableService(service);
                    checker.setScriptFilePath(f.getPath());
                    return checker;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        c -> c.getVulnerableService().getName(),
                        c -> c,
                        (a, b) -> a
                ));
    }

    /**
     * Синхронизация таблицы уязвимых сервисов
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

    /**
     * Синхронизация таблицы чекеров
     */
    private void syncCheckers(Map<String, CheckerEntity> detected) {
        List<CheckerEntity> existing = checkerRepository.findAll();

        // добавить новые
        for (CheckerEntity c : detected.values()) {
            boolean exists = existing.stream()
                    .anyMatch(e -> e.getVulnerableService().getName().equals(c.getVulnerableService().getName()));
            if (!exists) {
                checkerRepository.save(c);
                log.info("Добавлен новый чекер: {}", c.getVulnerableService().getName());
            }
        }

        // удалить отсутствующие
        for (CheckerEntity c : existing) {
            if (!detected.containsKey(c.getVulnerableService().getName())) {
                checkerRepository.delete(c);
                log.info("Удален чекер: {}", c.getVulnerableService().getName());
            }
        }
    }
}



