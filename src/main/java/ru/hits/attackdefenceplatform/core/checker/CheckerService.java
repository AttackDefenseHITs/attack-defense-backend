package ru.hits.attackdefenceplatform.core.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerService {
    private final String checkersDirectory = "/var/lib/checkers/";

    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final CheckerRepository checkerRepository;
    private final CheckerValidator checkerValidator;

    public void uploadChecker(String scriptText, UUID serviceId) throws IOException, InterruptedException {
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        Optional<CheckerEntity> existingCheckerOptional = checkerRepository.findByVulnerableServiceId(serviceId);

        Path checkersDirPath = Paths.get(checkersDirectory);
        if (!Files.exists(checkersDirPath)) {
            Files.createDirectories(checkersDirPath);
            log.info("Created directory for checkers: {}", checkersDirectory);
        }

        var fileName = UUID.randomUUID() + "_checker.py";
        var scriptPath = Paths.get(checkersDirectory, fileName);

        Files.writeString(scriptPath, scriptText);

        if (!checkerValidator.validate(scriptPath.toFile())) {
            throw new IllegalArgumentException("Checker script is invalid");
        }

        if (existingCheckerOptional.isPresent()) {
            CheckerEntity existingChecker = existingCheckerOptional.get();
            existingChecker.setScriptFilePath(scriptPath.toString());
            checkerRepository.save(existingChecker);
            log.info("Checker for service {} already exists. Path updated to {}", service.getName(), scriptPath);
        } else {
            CheckerEntity newChecker = new CheckerEntity();
            newChecker.setVulnerableService(service);
            newChecker.setScriptFilePath(scriptPath.toString());
            checkerRepository.save(newChecker);
            log.info("New checker script for service {} saved successfully at {}", service.getName(), scriptPath);
        }
    }

    public String getCheckerScriptByServiceId(UUID serviceId) throws IOException {
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId)
                .orElse(null);

        if (checkerEntity == null) {
            return ""; //добавить интерфейс для чекера
        }

        var scriptPath = Paths.get(checkerEntity.getScriptFilePath());

        return Files.readString(scriptPath);
    }

}


