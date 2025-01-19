package ru.hits.attackdefenceplatform.core.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.virtual_machine.VirtualMachineService;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerService {
    private final CheckerFileService checkerFileService;
    private final CheckerValidator checkerValidator;

    private final CheckerRepository checkerRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final TeamRepository teamRepository;
    private final VirtualMachineService virtualMachineService;

    public void uploadChecker(String scriptText, UUID serviceId) throws IOException, InterruptedException {
        var service = findServiceById(serviceId);
        var existingCheckerOptional = checkerRepository.findByVulnerableServiceId(serviceId);

        var scriptPath = checkerFileService.saveScriptToFile(scriptText);

        if (!checkerValidator.validate(scriptPath.toFile())) {
            throw new IllegalArgumentException("Checker script is invalid");
        }

        saveChecker(service, existingCheckerOptional, scriptPath);
    }

    public String getCheckerScriptByServiceId(UUID serviceId) throws IOException {
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId).orElse(null);
        return checkerEntity == null ? "" : checkerFileService.readScriptFromFile(checkerEntity.getScriptFilePath());
    }

    public void runChecker(UUID serviceId, UUID teamId, String command) {
        try {
            var service = vulnerableServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new IllegalArgumentException("Service not found"));
            var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId)
                    .orElseThrow(() -> new IllegalArgumentException("Checker not found for the given service ID"));
            var team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Team not found"));
            var virtualMachine = virtualMachineService.getVirtualMachinesByTeam(teamId)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Virtual Machine not found"));

            var targetIp = virtualMachine.ipAddress();
            int targetPort = service.getPort();

            log.info("Running checker for service: {} team: {}", service.getName(), team.getName());
            var result = executeCheckerScript(checkerEntity.getScriptFilePath(), command, targetIp, targetPort);

            handleCheckerResult(serviceId, teamId, result);
        } catch (Exception e) {
            log.error("Error running checker: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to run checker", e);
        }
    }

    private CheckerResult executeCheckerScript(
            String scriptPath,
            String command,
            String targetIp,
            int targetPort
    ) throws IOException, InterruptedException {
        var processBuilder = new ProcessBuilder("python3", scriptPath, command, targetIp, String.valueOf(targetPort));
        processBuilder.redirectErrorStream(true);

        var process = processBuilder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(log::info);
        }

        int exitCode = process.waitFor();
        return CheckerResult.fromCode(exitCode);
    }

    private void handleCheckerResult(UUID serviceId, UUID teamId, CheckerResult result) {
        switch (result) {
            case OK -> {
                log.info("Checker OK for service: {}, team: {}", serviceId, teamId);
                createFlags(serviceId, teamId);
                notifyClients(serviceId, teamId, "Checker passed");
            }
            case MUMBLE, CORRUPT, DOWN -> {
                log.warn("Checker issue detected for service: {}, team: {}, result: {}", serviceId, teamId, result);
                notifyClients(serviceId, teamId, "Checker reported an issue: " + result);
            }
            case CHECK_FAILED -> {
                log.error("Checker failed for service: {}, team: {}", serviceId, teamId);
                notifyClients(serviceId, teamId, "Checker execution failed");
            }
        }
    }

    private void createFlags(UUID serviceId, UUID teamId) {
        // Логика создания флагов (будет позже)
        log.info("Flags created for service: {}, team: {}", serviceId, teamId);
    }

    private void notifyClients(UUID serviceId, UUID teamId, String message) {
        // Логика отправки уведомлений (будет позже)
        log.info("Notification sent for service: {}, team: {}, message: {}", serviceId, teamId, message);
    }

    private VulnerableServiceEntity findServiceById(UUID serviceId) {
        return vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

    private void saveChecker(VulnerableServiceEntity service,
                             Optional<CheckerEntity> existingCheckerOptional,
                             Path scriptPath) {
        if (existingCheckerOptional.isPresent()) {
            var existingChecker = existingCheckerOptional.get();
            existingChecker.setScriptFilePath(scriptPath.toString());
            checkerRepository.save(existingChecker);
            log.info("Checker for service {} already exists. Path updated to {}", service.getName(), scriptPath);
        } else {
            var newChecker = new CheckerEntity();
            newChecker.setVulnerableService(service);
            newChecker.setScriptFilePath(scriptPath.toString());
            checkerRepository.save(newChecker);
            log.info("New checker script for service {} saved successfully at {}", service.getName(), scriptPath);
        }
    }
}





