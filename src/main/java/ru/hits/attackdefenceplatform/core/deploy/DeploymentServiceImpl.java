package ru.hits.attackdefenceplatform.core.deploy;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineEntity;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentServiceImpl implements DeploymentService{
    private final VirtualMachineRepository virtualMachineRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;

    @Override
    public void deployAllServices() {
        var virtualMachines = virtualMachineRepository.findAll();
        var vulnerableServices = vulnerableServiceRepository.findAll();

        for (var vm : virtualMachines) {
            for (var service : vulnerableServices) {
                try {
                    deployServiceToVirtualMachine(service, vm);
                } catch (Exception e) {
                    log.error("Ошибка при деплое сервиса '{}' на виртуальную машину '{}': {}",
                            service.getName(), vm.getIpAddress(), e.getMessage());
                }
            }
        }
    }

    private void deployServiceToVirtualMachine(VulnerableServiceEntity service, VirtualMachineEntity vm) throws Exception {
        log.info("Деплой сервиса '{}' на виртуальную машину '{}'.", service.getName(), vm.getIpAddress());

        // Команда для выполнения docker-compose up
        String command = String.format(
                "git clone %s /opt/%s && cd /opt/%s && docker-compose up -d --build",
                service.getGitRepositoryUrl(),
                service.getName(),
                service.getName()
        );

        executeCommandOverSSH(vm.getIpAddress(), vm.getUsername(), vm.getPassword(), command);

        log.info("Сервис '{}' успешно задеплоен на '{}'.", service.getName(), vm.getIpAddress());
    }


    private void executeCommandOverSSH(String host, String username, String password, String command) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, 22);
        session.setPassword(password);

        // Настройка безопасности, чтобы избежать проверки known_hosts
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // Открываем канал для выполнения команды
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        // Перенаправление вывода
        channel.setErrStream(System.err);
        channel.setOutputStream(System.out);

        channel.connect();

        while (!channel.isClosed()) {
            Thread.sleep(100);
        }

        int exitStatus = channel.getExitStatus();
        channel.disconnect();
        session.disconnect();

        if (exitStatus != 0) {
            throw new RuntimeException("Команда завершилась с ошибкой, код: " + exitStatus);
        }
    }
}
