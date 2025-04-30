package ru.hits.attackdefenceplatform.core.checker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.handler.CheckerResultHandler;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.core.checker.script.CheckerFileService;
import ru.hits.attackdefenceplatform.core.checker.script.CheckerLinter;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerServiceImpl implements CheckerService {
    private final CheckerFileService checkerFileService;
    private final CheckerLinter checkerLinter;
    private final CheckerResultHandler checkerResultHandler;
    private final ScriptExecutor scriptExecutor;

    private final CheckerRepository checkerRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final VirtualMachineService virtualMachineService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void uploadChecker(String scriptText, UUID serviceId) throws IOException {
        var service = findServiceById(serviceId);
        var existingCheckerOptional = checkerRepository.findByVulnerableServiceId(serviceId);

        var scriptPath = checkerFileService.saveScriptToFile(scriptText);

        if (!checkerLinter.validate(scriptPath.toFile())) {
            throw new IllegalArgumentException("Скрипт чекера недействителен");
        }

        saveChecker(service, existingCheckerOptional, scriptPath);
    }

    @Override
    public String getCheckerScriptByServiceId(UUID serviceId) throws IOException {
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId).orElse(null);
        return checkerEntity == null ? "" : checkerFileService.readScriptFromFilePath(checkerEntity.getScriptFilePath());
    }

    @Override
    @Async("taskExecutor")
    public void runChecker(UUID serviceId, UUID teamId, List<String> commands) {
        var executionData = prepareCheckerExecution(serviceId, teamId, commands);
        executeChecker(
                executionData.getVirtualMachine(),
                executionData.getCheckerEntity(),
                executionData.getService(),
                executionData.getCommand()
        );
    }

    @Override
    @Async("taskExecutor")
    public void runAllCheckers(List<String> commands) {
        var allCheckers = checkerRepository.findAll();
        var allVirtualMachines = virtualMachineService.getAllVirtualMachines();

        if (allCheckers.isEmpty()) {
            log.warn("Чекеры не найдены для выполнения.");
            return;
        }

        if (allVirtualMachines.isEmpty()) {
            log.warn("Виртуальные машины не найдены для выполнения чекеров.");
            return;
        }

        log.info("Запуск выполнения всех чекеров. Всего чекеров: {}, Всего виртуальных машин: {}", allCheckers.size(), allVirtualMachines.size());

        allCheckers.forEach(checker -> {
            var service = checker.getVulnerableService();

            executorService.submit(() -> {
                try {
                    for (var vm : allVirtualMachines) {
                        var executionData = new ExecutionData(service, checker, vm, String.join(" ", commands));
                        executeChecker(
                                executionData.getVirtualMachine(),
                                executionData.getCheckerEntity(),
                                executionData.getService(),
                                executionData.getCommand()
                        );
                    }
                } catch (Exception e) {
                    log.error("Не удалось выполнить чекер для сервиса с ID {}: {}", checker.getVulnerableService().getId(), e.getMessage(), e);
                }
            });
        });

        log.info("Все чекеры отправлены на выполнение на всех виртуальных машинах.");
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

            log.info("Запуск чекера для сервиса: {} на виртуальной машине с IP: {}", service.getName(), targetIp);
            var result = scriptExecutor.executeScript(
                    checkerEntity.getScriptFilePath(),
                    command,
                    targetIp,
                    targetPort
            );

            checkerResultHandler.handleCheckerResult(service.getId(), virtualMachine.teamId(), result);
        } catch (Exception e) {
            log.error("Ошибка при запуске чекера: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось запустить чекер", e);
        }
    }

    private ExecutionData prepareCheckerExecution(UUID serviceId, UUID teamId, List<String> commands) {
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Сервис не найден"));
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Чекер не найден для данного ID сервиса"));
        var virtualMachine = virtualMachineService.getVirtualMachinesByTeam(teamId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Виртуальная машина не найдена"));

        var command = String.join(" ", commands);

        return new ExecutionData(service, checkerEntity, virtualMachine, command);
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
            log.info("Чекер для сервиса {} уже существует. Путь обновлён на {}", service.getName(), scriptPath);
        } else {
            var newChecker = new CheckerEntity();
            newChecker.setVulnerableService(service);
            newChecker.setScriptFilePath(scriptPath.toString());
            checkerRepository.save(newChecker);
            log.info("Новый скрипт чекера для сервиса {} успешно сохранён по пути {}", service.getName(), scriptPath);
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







