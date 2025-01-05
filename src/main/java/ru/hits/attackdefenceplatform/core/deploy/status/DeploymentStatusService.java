package ru.hits.attackdefenceplatform.core.deploy.status;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.deploy.repository.DeploymentStatusEntity;
import ru.hits.attackdefenceplatform.core.deploy.repository.DeploymentStatusRepository;
import ru.hits.attackdefenceplatform.core.deploy.status.mapper.DeploymentStatusInitializer;
import ru.hits.attackdefenceplatform.core.user.repository.Role;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.core.virtual_machine.mapper.VirtualMachineMapper;
import ru.hits.attackdefenceplatform.core.vulnerable_service.mapper.VulnerableServiceMapper;
import ru.hits.attackdefenceplatform.public_interface.deployment.DeploymentDataDto;
import ru.hits.attackdefenceplatform.public_interface.deployment.DeploymentResult;
import ru.hits.attackdefenceplatform.public_interface.deployment.DeploymentStatusDto;
import ru.hits.attackdefenceplatform.websocket.client.WebSocketClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentStatusService {
    private final DeploymentStatusInitializer deploymentStatusInitializer;
    private final DeploymentStatusRepository deploymentStatusRepository;
    private final WebSocketClient<DeploymentResult> deploymentWebSocketClient;
    private final UserRepository userRepository;

    /**
     * Заполняет таблицу с результатами деплоя при инициализации приложения
     */
    @PostConstruct
    public void initDeploymentStatuses() {
        deploymentStatusInitializer.initializeStatusesForAllCombinations();
    }

    /**
     * Обновляет все статусы перед массовым деплоем
     */
    public void updateAllStatusesBeforeAllDeployment(){
        deploymentStatusInitializer.setAllStatusesToPending();
        deploymentWebSocketClient.sendNotification(getAllDeploymentResults(), getAdminsIds());
    }

    /**
     * Возвращает список всех DeploymentResult
     */
    public DeploymentResult getAllDeploymentResults() {
        var statuses = deploymentStatusRepository.findAll();

        var statusesDto = statuses.stream()
                .map(this::mapToDeploymentData)
                .toList();

        return new DeploymentResult(statusesDto);
    }

    /**
     * Обновляет данные в базе и отправляет обновление через WebSocket
     *
     * @param deploymentStatusDto входящая моделька с данными для обновления
     */
    public void updateDeploymentStatus(DeploymentStatusDto deploymentStatusDto) {
        var entityOptional = deploymentStatusRepository
                .findByVirtualMachineIdAndVulnerableServiceId(
                        deploymentStatusDto.virtualMachineId(),
                        deploymentStatusDto.vulnerableServiceId()
                );

        if (entityOptional.isEmpty()) {
            throw new IllegalArgumentException("Статус деплоя для данной комбинации не найден");
        }

        var entity = entityOptional.get();
        entity.setDeploymentStatus(deploymentStatusDto.status());
        entity.setMessage(deploymentStatusDto.message());
        entity.setUpdatedAt(LocalDateTime.now());

        var newEntity = deploymentStatusRepository.save(entity);

        var deploymentData = new DeploymentDataDto(
                VirtualMachineMapper.toDto(newEntity.getVirtualMachine()),
                VulnerableServiceMapper.toDto(newEntity.getVulnerableService()),
                newEntity.getDeploymentStatus(),
                newEntity.getMessage(),
                newEntity.getUpdatedAt()
        );

        var adminIds = getAdminsIds();
        deploymentWebSocketClient.sendNotification(getDeploymentResultFromDto(deploymentData), adminIds);

        log.info("Статус деплоя обновлен и отправлен по WebSocket");
    }


    /**
     * Преобразует DeploymentStatusEntity в DeploymentDataDto
     */
    private DeploymentDataDto mapToDeploymentData(DeploymentStatusEntity entity) {
        return new DeploymentDataDto(
                VirtualMachineMapper.toDto(entity.getVirtualMachine()),
                VulnerableServiceMapper.toDto(entity.getVulnerableService()),
                entity.getDeploymentStatus(),
                entity.getMessage(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Получает список id администраторов
     */
    private List<String> getAdminsIds() {
        var admins = userRepository.findAllByRole(Role.ADMIN);

        return admins.stream()
                .map(admin -> admin.getId().toString())
                .toList();
    }

    private DeploymentResult getDeploymentResultFromDto(DeploymentDataDto data){
        return new DeploymentResult(List.of(data));
    }
}
