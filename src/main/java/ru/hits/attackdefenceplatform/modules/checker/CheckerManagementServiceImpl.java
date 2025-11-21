package ru.hits.attackdefenceplatform.modules.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hits.attackdefenceplatform.modules.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.modules.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.modules.checker.script.CheckerFileService;
import ru.hits.attackdefenceplatform.modules.checker.script.CheckerFileServiceNew;
import ru.hits.attackdefenceplatform.modules.checker.script.CheckerLinter;
import ru.hits.attackdefenceplatform.modules.repo.RepositoryAdapter;
import ru.hits.attackdefenceplatform.modules.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerManagementServiceImpl implements CheckerManagementService {

    private final CheckerRepository checkerRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final CheckerFileService checkerFileService;

    private final CheckerLinter checkerLinter;
    private final RepositoryAdapter repositoryAdapter;
    private final CheckerFileServiceNew checkerFileServiceNew;

    /**
     * Загрузка или обновление чекера для сервиса.
     */
    @Override
    @Transactional
    public void uploadChecker(String scriptText, UUID serviceId) throws IOException {
        var service = findServiceById(serviceId);
        var existingChecker = checkerRepository.findByVulnerableServiceId(serviceId);

        var scriptPath = checkerFileService.saveScriptToFile(scriptText);

        if (!checkerLinter.validate(scriptPath.toFile())) {
            throw new IllegalArgumentException("Скрипт чекера недействителен");
        }

        saveChecker(service, existingChecker, scriptPath);
    }

    @Override
    @Transactional
    public void syncCheckersFromRepository(
            String repoName,
            String branch,
            Map<String, List<RepoFileDto>> detected
    ) throws IOException {
        Map<String, CheckerEntity> existing = checkerRepository.findAll().stream()
                .collect(Collectors.toMap(
                        c -> c.getVulnerableService().getName().toLowerCase(),
                        c -> c));

        Set<String> processed = new HashSet<>();

        for (var entry : detected.entrySet()) {

            String serviceName = entry.getKey().toLowerCase();
            List<RepoFileDto> files = entry.getValue();

            VulnerableServiceEntity service = vulnerableServiceRepository.findByName(serviceName)
                    .orElseThrow();

            Path tempDir = Files.createTempDirectory("checker_sync_");

            for (RepoFileDto file : files) {
                byte[] content = repositoryAdapter.getFileContent(repoName, file.getPath(), branch);
                Path relative = Paths.get(file.getPath()).subpath(2, Paths.get(file.getPath()).getNameCount());
                Path out = tempDir.resolve(relative);

                Files.createDirectories(out.getParent());
                Files.write(out, content);
            }

            Path storedRoot = checkerFileServiceNew.saveCheckerDirectory(tempDir, serviceName);

            CheckerEntity checker = existing.get(serviceName);
            if (checker == null) {
                checker = new CheckerEntity();
                checker.setVulnerableService(service);
            }
            checker.setScriptFilePath(storedRoot.resolve("run.py").toString());

            checkerRepository.save(checker);
            processed.add(serviceName);
        }

        // cleanup
        existing.values().stream()
                .filter(c -> !processed.contains(c.getVulnerableService().getName().toLowerCase()))
                .forEach(checkerRepository::delete);
    }

    /**
     * Получить текст скрипта чекера.
     */
    @Override
    @Transactional(readOnly = true)
    public String getCheckerScript(UUID serviceId) throws IOException {
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId).orElse(null);
        return checkerEntity == null ? "" : checkerFileService.readScriptFromFilePath(checkerEntity.getScriptFilePath());
    }

    private VulnerableServiceEntity findServiceById(UUID serviceId) {
        return vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Сервис не найден"));
    }

    private void saveChecker(VulnerableServiceEntity service,
                             Optional<CheckerEntity> existingCheckerOptional,
                             Path scriptPath) {
        if (existingCheckerOptional.isPresent()) {
            var existingChecker = existingCheckerOptional.get();
            checkerFileService.deleteScriptFile(existingChecker.getScriptFilePath());
            existingChecker.setScriptFilePath(scriptPath.toString());
            checkerRepository.save(existingChecker);
            log.info("Чекер для сервиса {} обновлён", service.getName());
        } else {
            var newChecker = new CheckerEntity();
            newChecker.setVulnerableService(service);
            newChecker.setScriptFilePath(scriptPath.toString());
            checkerRepository.save(newChecker);
            log.info("Чекер для сервиса {} добавлен", service.getName());
        }
    }
}

