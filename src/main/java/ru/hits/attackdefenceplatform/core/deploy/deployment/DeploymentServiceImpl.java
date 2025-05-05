package ru.hits.attackdefenceplatform.core.deploy.deployment;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.deploy.deployment.DeploymentService;
import ru.hits.attackdefenceplatform.core.deploy.enums.DeploymentStatus;
import ru.hits.attackdefenceplatform.core.deploy.status.DeploymentStatusService;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineEntity;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.deployment.DeploymentStatusDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentServiceImpl implements DeploymentService {
    private final VirtualMachineRepository virtualMachineRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final DeploymentStatusService deploymentStatusService;
    private final ScriptBuilder scriptBuilder;

    @Qualifier("taskExecutor")
    private final Executor taskExecutor;

    private final AtomicBoolean isDeploymentInProgress = new AtomicBoolean(false);

    @Async("taskExecutor")
    @Override
    public void deployAllServices() {
        if (!isDeploymentInProgress.compareAndSet(false, true)) {
            log.warn("Деплой всех сервисов уже запущен.");
            return;
        }

        deploymentStatusService.updateAllStatusesBeforeAllDeployment();
        log.info("Деплой всех сервисов запущен.");

        try {
            var virtualMachines = virtualMachineRepository.findAll();
            var vulnerableServices = vulnerableServiceRepository.findAll();

            List<CompletableFuture<Void>> deploymentTasks = new ArrayList<>();

            for (var vm : virtualMachines) {
                deploymentTasks.add(CompletableFuture.runAsync(() -> {
                    try {
                        deployServicesToVirtualMachine(vulnerableServices, vm);
                    } catch (Exception e) {
                        log.error("Ошибка при деплое сервисов на виртуальную машину '{}': {}", vm.getIpAddress(), e.getMessage(), e);
                    }
                }, taskExecutor));
            }

            CompletableFuture.allOf(deploymentTasks.toArray(new CompletableFuture[0])).get(1, TimeUnit.HOURS);

        } catch (TimeoutException e) {
            log.error("Деплой не завершился за установленное время.");
        } catch (Exception e) {
            log.error("Ошибка при выполнении деплоя всех сервисов: {}", e.getMessage(), e);
        } finally {
            isDeploymentInProgress.set(false);
        }
    }

    @Override
    @Async("taskExecutor")
    public void deployServiceOnVirtualMachine(UUID serviceId, UUID virtualMachineId) {
        if (!isDeploymentInProgress.compareAndSet(false, true)) {
            log.warn("Деплой сервиса уже запущен.");
            return;
        }

        var vm = virtualMachineRepository.findById(virtualMachineId)
                .orElseThrow(() -> new IllegalArgumentException("Виртуальная машина с ID '" + virtualMachineId + "' не найдена"));
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Сервис с ID '" + serviceId + "' не найден"));

        log.info("Начинаем деплой сервиса '{}' на виртуальную машину '{}'.", service.getName(), vm.getIpAddress());

        try {
            deployServicesToVirtualMachine(List.of(service), vm);
        } catch (Exception e) {
            log.error("Ошибка при передеплое сервиса '{}' на виртуальной машине '{}': {}",
                    service.getName(), vm.getIpAddress(), e.getMessage(), e);
        } finally {
            isDeploymentInProgress.set(false);
        }
    }

    @Override
    public Boolean isDeploymentInProgress() {
        return isDeploymentInProgress.get();
    }

    private void deployServicesToVirtualMachine(
            List<VulnerableServiceEntity> services,
            VirtualMachineEntity vm
    ) {
        log.info("Начинаем деплой на виртуальную машину '{}'.", vm.getIpAddress());

        for (var service : services) {
            try {
                deploymentStatusService.updateDeploymentStatus(
                        new DeploymentStatusDto(
                                vm.getId(),
                                service.getId(),
                                DeploymentStatus.IN_PROGRESS,
                                "Начат деплой сервиса " + service.getName()
                        )
                );

                String deploymentScript = scriptBuilder.buildDeploymentScript(service);

                sendAndExecuteScript(vm.getIpAddress(), vm.getUsername(), vm.getPassword(), deploymentScript);

                boolean serviceIsUp = isServiceUp(vm.getIpAddress(), service.getPort());

                if (serviceIsUp) {
                    deploymentStatusService.updateDeploymentStatus(
                            new DeploymentStatusDto(
                                    vm.getId(),
                                    service.getId(),
                                    DeploymentStatus.SUCCESS,
                                    "Сервис " + service.getName() + " успешно задеплоен и работает на порту " + service.getPort()
                            )
                    );
                    log.info("Сервис '{}' успешно задеплоен на '{}', доступен на порту '{}'.", service.getName(), vm.getIpAddress(), service.getPort());
                } else {
                    deploymentStatusService.updateDeploymentStatus(
                            new DeploymentStatusDto(
                                    vm.getId(),
                                    service.getId(),
                                    DeploymentStatus.FAILURE,
                                    "Сервис " + service.getName() + " не смог подняться на порту " + service.getPort()
                            )
                    );
                    log.error("Сервис '{}' не доступен на виртуальной машине '{}' на порту '{}'.", service.getName(), vm.getIpAddress(), service.getPort());
                }
            } catch (Exception e) {
                deploymentStatusService.updateDeploymentStatus(
                        new DeploymentStatusDto(
                                vm.getId(),
                                service.getId(),
                                DeploymentStatus.FAILURE,
                                "Ошибка деплоя сервиса " + service.getName() + ": " + e.getMessage()
                        )
                );

                log.error("Ошибка деплоя сервиса '{}' на '{}': {}", service.getName(), vm.getIpAddress(), e.getMessage(), e);
            }
        }
    }

    private void sendAndExecuteScript(String host, String username, String password, String scriptContent) throws Exception {
        var jsch = new JSch();
        Session session = jsch.getSession(username, host, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        try {
            var channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            var scriptPath = "/tmp/deploy_services.sh";

            try (var inputStream = new ByteArrayInputStream(scriptContent.getBytes(StandardCharsets.UTF_8))) {
                channelSftp.put(inputStream, scriptPath);
            }

            channelSftp.disconnect();
            log.info("Скрипт деплоя успешно отправлен на '{}'.", host);

            var channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand("bash " + scriptPath);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

            channelExec.setOutputStream(outputStream);
            channelExec.setErrStream(errorStream);

            channelExec.connect();

            while (!channelExec.isClosed()) {
                Thread.sleep(100);
            }

            int exitStatus = channelExec.getExitStatus();
            channelExec.disconnect();

            String output = outputStream.toString(StandardCharsets.UTF_8);
            String errors = errorStream.toString(StandardCharsets.UTF_8);

            if (!output.isBlank()) {
                log.info("Вывод скрипта на '{}':\n{}", host, output);
            }

            if (!errors.isBlank()) {
                log.warn("Ошибки скрипта на '{}':\n{}", host, errors);
            }

            if (exitStatus != 0) {
                throw new RuntimeException("Скрипт завершился с ошибкой, код: " + exitStatus);
            }

            log.info("Скрипт успешно выполнен на '{}'.", host);
        } finally {
            session.disconnect();
        }
    }

    private boolean isServiceUp(String host, int port) {
        int attempts = 0;
        while (attempts < 10) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 5000);
                return true;
            } catch (IOException e) {
                attempts++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }
}





