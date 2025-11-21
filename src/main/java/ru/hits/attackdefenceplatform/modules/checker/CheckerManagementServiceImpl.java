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
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerManagementServiceImpl implements CheckerManagementService {

    private final CheckerRepository checkerRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final CheckerFileService checkerFileService;
    private final CheckerFileServiceNew checkerFileServiceNew;

    private final CheckerLinter checkerLinter;

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
    public void uploadChecker(MultipartFile scriptArchive, UUID serviceId) throws IOException {
        var service = findServiceById(serviceId);

        // 1. Распаковываем архив чекера во временную директорию
        Path tempDir = checkerFileServiceNew.extractCheckerArchive(scriptArchive);

        // 3. Сохраняем файлы в платформу
        Path storedFiles = checkerFileServiceNew.saveCheckerDirectory(tempDir, service.getName());

        log.info("Чекер для сервиса '{}' успешно загружен ({} файлов)",
                service.getName(), storedFiles);
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

