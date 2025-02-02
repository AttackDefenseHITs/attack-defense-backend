package ru.hits.attackdefenceplatform.core.deploy.deployment;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentServiceImpl implements DeploymentService {
    private final VirtualMachineRepository virtualMachineRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final DeploymentStatusService deploymentStatusService;

    @Setter
    private volatile boolean isDeploymentInProgress = false;

    @Async("taskExecutor")
    @Override
    public void deployAllServices() {
        if (isDeploymentInProgress) {
            log.warn("Деплой всех сервисов уже запущен.");
            return;
        }

        isDeploymentInProgress = true;
        deploymentStatusService.updateAllStatusesBeforeAllDeployment();
        log.info("Деплой всех сервисов запущен.");

        try {
            var virtualMachines = virtualMachineRepository.findAll();
            var vulnerableServices = vulnerableServiceRepository.findAll();

            ExecutorService executorService = Executors.newFixedThreadPool(virtualMachines.size());

            for (var vm : virtualMachines) {
                executorService.submit(() -> {
                    try {
                        deployServicesToVirtualMachine(vulnerableServices, vm);
                    } catch (Exception e) {
                        log.error("Ошибка при деплое сервисов на виртуальную машину '{}': {}",
                                vm.getIpAddress(), e.getMessage()
                        );
                    }
                });
            }

            executorService.shutdown();
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                log.error("Деплой не завершился за установленное время.");
            }
        } catch (Exception e) {
            log.error("Ошибка при выполнении деплоя всех сервисов: {}", e.getMessage());
        } finally {
            setDeploymentInProgress(false);
        }
    }

    @Override
    @Async("taskExecutor")
    public void deployServiceOnVirtualMachine(UUID serviceId, UUID virtualMachineId) {
        if (isDeploymentInProgress()) {
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
                    service.getName(), vm.getIpAddress(), e.getMessage()
            );
        }
    }

    @Override
    public Boolean isDeploymentInProgress() {
        return isDeploymentInProgress;
    }

    private void deployServicesToVirtualMachine(
            List<VulnerableServiceEntity> services,
            VirtualMachineEntity vm
    ) throws Exception {
        log.info("Начинаем деплой на виртуальную машину '{}'.", vm.getIpAddress());

        for (var service : services) {
            try {
                // Уведомляем, что деплой начался
                deploymentStatusService.updateDeploymentStatus(
                        new DeploymentStatusDto(
                                vm.getId(),
                                service.getId(),
                                DeploymentStatus.IN_PROGRESS,
                                "Начат деплой сервиса " + service.getName()
                        )
                );

                // Создание скрипта деплоя для текущего сервиса
                String deploymentScript = String.format(
                        "if [ -d \"/opt/%s\" ]; then\n" +
                                "  echo \"Обновление сервиса '%s'...\";\n" +
                                "  cd /opt/%s && git pull && docker-compose up -d --build;\n" +
                                "else\n" +
                                "  echo \"Деплой нового сервиса '%s'...\";\n" +
                                "  git clone %s /opt/%s && cd /opt/%s && docker-compose up -d --build;\n" +
                                "fi\n",
                        service.getName(),  // Проверка на существование папки
                        service.getName(),  // Лог обновления
                        service.getName(),  // Путь для pull
                        service.getName(),  // Лог деплоя нового сервиса
                        service.getGitRepositoryUrl(), // URL репозитория
                        service.getName(),  // Путь для clone
                        service.getName()   // Путь для docker-compose
                );

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

                log.error("Ошибка деплоя сервиса '{}' на '{}': {}", service.getName(), vm.getIpAddress(), e.getMessage());
            }
        }
    }


    private void sendAndExecuteScript(
            String host,
            String username,
            String password,
            String scriptContent
    ) throws Exception {
        var jsch = new JSch();
        Session session = jsch.getSession(username, host, 22);
        session.setPassword(password);

        // Настройка безопасности
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        try {
            // Открываем канал для передачи файла
            var channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // Путь для скрипта на удаленной машине
            var scriptPath = "/tmp/deploy_services.sh";

            // Загружаем скрипт на удаленную машину
            try (var inputStream = new ByteArrayInputStream(scriptContent.getBytes())) {
                channelSftp.put(inputStream, scriptPath);
            }

            channelSftp.disconnect();
            log.info("Скрипт деплоя успешно отправлен на '{}'.", host);

            // Выполнение скрипта
            var channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand("bash " + scriptPath);
            channelExec.setErrStream(System.err);
            channelExec.setOutputStream(System.out);

            channelExec.connect();

            while (!channelExec.isClosed()) {
                Thread.sleep(100);
            }

            var exitStatus = channelExec.getExitStatus();
            channelExec.disconnect();

            if (exitStatus != 0) {
                throw new RuntimeException("Скрипт завершился с ошибкой, код: " + exitStatus);
            }

            log.info("Скрипт успешно выполнен на '{}'.", host);
        } finally {
            session.disconnect();
        }
    }

    private boolean isServiceUp(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}



