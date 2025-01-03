package ru.hits.attackdefenceplatform.core.deploy.deployment;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.deploy.deployment.DeploymentService;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineEntity;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentServiceImpl implements DeploymentService {
    private final VirtualMachineRepository virtualMachineRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;

    @Override
    public void deployAllServices() {
        var virtualMachines = virtualMachineRepository.findAll();
        var vulnerableServices = vulnerableServiceRepository.findAll();

        // Создаем пул потоков с количеством потоков, равным числу виртуальных машин
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

        // Ожидание завершения всех задач
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                log.error("Деплой не завершился за установленное время.");
            }
        } catch (InterruptedException e) {
            log.error("Пул потоков был прерван: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void deployServiceOnVirtualMachine(String serviceName, String virtualMachineIp) {
        var vm = virtualMachineRepository.findByIpAddress(virtualMachineIp)
                .orElseThrow(() -> new IllegalArgumentException("Виртуальная машина с IP '" + virtualMachineIp + "' не найдена"));
        var service = vulnerableServiceRepository.findByName(serviceName)
                .orElseThrow(() -> new IllegalArgumentException("Сервис с именем '" + serviceName + "' не найден"));

        log.info("Начинаем деплой сервиса '{}' на виртуальную машину '{}'.", serviceName, virtualMachineIp);

        try {
            deployServicesToVirtualMachine(List.of(service), vm);
            log.info("Сервис '{}' успешно передеплоен на виртуальной машине '{}'.",
                    serviceName, virtualMachineIp
            );
        } catch (Exception e) {
            log.error("Ошибка при передеплое сервиса '{}' на виртуальной машине '{}': {}",
                    serviceName, virtualMachineIp, e.getMessage()
            );
        }
    }

    private void deployServicesToVirtualMachine(
            List<VulnerableServiceEntity> services,
            VirtualMachineEntity vm
    ) throws Exception {
        log.info("Начинаем деплой всех сервисов на виртуальную машину '{}'.", vm.getIpAddress());

        // Создание скрипта деплоя
        var deploymentScript = new StringBuilder();
        for (var service : services) {
            deploymentScript.append(String.format(
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
            ));
        }

        // Отправка и выполнение скрипта в рамках одной SSH-сессии
        sendAndExecuteScript(vm.getIpAddress(), vm.getUsername(), vm.getPassword(), deploymentScript.toString());

        log.info("Все сервисы успешно задеплоены или обновлены на '{}'.", vm.getIpAddress());
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
}



