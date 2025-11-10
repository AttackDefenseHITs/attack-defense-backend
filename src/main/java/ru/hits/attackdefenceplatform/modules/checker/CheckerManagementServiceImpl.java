package ru.hits.attackdefenceplatform.modules.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.modules.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.modules.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.modules.checker.script.CheckerFileService;
import ru.hits.attackdefenceplatform.modules.checker.script.CheckerLinter;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerManagementServiceImpl implements CheckerManagementService {

    private final CheckerRepository checkerRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final CheckerFileService checkerFileService;
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

