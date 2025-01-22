package ru.hits.attackdefenceplatform.core.checker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.handler.CheckerResultHandler;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.core.checker.script.CheckerFileService;
import ru.hits.attackdefenceplatform.core.checker.script.CheckerValidator;
import ru.hits.attackdefenceplatform.core.checker.script.ScriptExecutor;
import ru.hits.attackdefenceplatform.core.virtual_machine.VirtualMachineService;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerServiceImpl implements CheckerService{
    private final CheckerFileService checkerFileService;
    private final CheckerValidator checkerValidator;
    private final CheckerResultHandler checkerResultHandler;
    private final ScriptExecutor scriptExecutor;

    private final CheckerRepository checkerRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final VirtualMachineService virtualMachineService;

    @Override
    public void uploadChecker(String scriptText, UUID serviceId) throws IOException {
        var service = findServiceById(serviceId);
        var existingCheckerOptional = checkerRepository.findByVulnerableServiceId(serviceId);

        var scriptPath = checkerFileService.saveScriptToFile(scriptText);

        if (!checkerValidator.validate(scriptPath.toFile())) {
            throw new IllegalArgumentException("Checker script is invalid");
        }

        saveChecker(service, existingCheckerOptional, scriptPath);
    }

    @Override
    public String getCheckerScriptByServiceId(UUID serviceId) throws IOException {
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId).orElse(null);
        return checkerEntity == null ? "" : checkerFileService.readScriptFromFile(checkerEntity.getScriptFilePath());
    }

    @Override
    public void runChecker(UUID serviceId, UUID teamId, List<String> commands) {
        var executionData = prepareCheckerExecution(serviceId, teamId, commands);
        executeChecker(
                executionData.getVirtualMachine(),
                executionData.getCheckerEntity(),
                executionData.getService(),
                executionData.getCommand()
        );
    }

    private void executeChecker(
            VirtualMachineDto virtualMachine,
            CheckerEntity checkerEntity,
            VulnerableServiceEntity service,
            String command
    ) {
        try {
            var targetIp = virtualMachine.ipAddress();
            int targetPort = service.getPort();

            log.info("Running checker for service: {} on virtual machine with IP: {}", service.getName(), targetIp);
            var result = scriptExecutor.executeScript(
                    checkerEntity.getScriptFilePath(),
                    command,
                    targetIp,
                    targetPort
            );

            checkerResultHandler.handleCheckerResult(service.getId(), virtualMachine.teamId(), result);
        } catch (Exception e) {
            log.error("Error running checker: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to run checker", e);
        }
    }

    private ExecutionData prepareCheckerExecution(UUID serviceId, UUID teamId, List<String> commands) {
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Checker not found for the given service ID"));
        var virtualMachine = virtualMachineService.getVirtualMachinesByTeam(teamId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Virtual Machine not found"));

        var command = String.join(" ", commands);

        return new ExecutionData(service, checkerEntity, virtualMachine, command);
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
            checkerFileService.deleteScriptFile(existingChecker.getScriptFilePath());
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

    @Data
    @AllArgsConstructor
    private static class ExecutionData {
        private VulnerableServiceEntity service;
        private CheckerEntity checkerEntity;
        private VirtualMachineDto virtualMachine;
        private String command;
    }
}






