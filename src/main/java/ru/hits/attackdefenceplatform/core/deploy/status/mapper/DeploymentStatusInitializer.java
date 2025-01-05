package ru.hits.attackdefenceplatform.core.deploy.status.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.deploy.enums.DeploymentStatus;
import ru.hits.attackdefenceplatform.core.deploy.repository.DeploymentStatusEntity;
import ru.hits.attackdefenceplatform.core.deploy.repository.DeploymentStatusRepository;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeploymentStatusInitializer {

    private final DeploymentStatusRepository deploymentStatusRepository;
    private final VirtualMachineRepository virtualMachineRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;

    /**
     * Метод для создания или обновления DeploymentStatusEntity.
     *
     * @param virtualMachineId    ID виртуальной машины
     * @param vulnerableServiceId ID сервиса
     */
    public DeploymentStatusEntity mapAndSaveIfNotExists(UUID virtualMachineId, UUID vulnerableServiceId) {
        var vm = virtualMachineRepository.findById(virtualMachineId)
                .orElseThrow(() -> new IllegalArgumentException("Virtual machine not found"));

        var service = vulnerableServiceRepository.findById(vulnerableServiceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        if (!deploymentStatusRepository.existsByVirtualMachineIdAndVulnerableServiceId(vm.getId(), service.getId())) {
            var statusEntity = new DeploymentStatusEntity();
            statusEntity.setVirtualMachine(vm);
            statusEntity.setVulnerableService(service);
            statusEntity.setDeploymentStatus(DeploymentStatus.PENDING);
            statusEntity.setMessage("Not deployed");
            statusEntity.setUpdatedAt(LocalDateTime.now());

            return deploymentStatusRepository.save(statusEntity);
        }

        return deploymentStatusRepository.findByVirtualMachineIdAndVulnerableServiceId(vm.getId(), service.getId())
                .orElseThrow(() -> new IllegalArgumentException("Deployment status not found"));
    }

    public List<DeploymentStatusEntity> initializeStatusesForNewVirtualMachine(UUID virtualMachineId) {
        var vm = virtualMachineRepository.findById(virtualMachineId)
                .orElseThrow(() -> new IllegalArgumentException("Virtual machine not found"));

        var services = vulnerableServiceRepository.findAll();

        return services.stream()
                .map(service -> mapAndSaveIfNotExists(vm.getId(), service.getId()))
                .toList();
    }

    public List<DeploymentStatusEntity> initializeStatusesForNewService(UUID serviceId) {
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        var virtualMachines = virtualMachineRepository.findAll();

        return virtualMachines.stream()
                .map(vm -> mapAndSaveIfNotExists(vm.getId(), service.getId()))
                .toList();
    }

    /**
     * Удаляет все записи для указанной виртуальной машины.
     *
     * @param virtualMachineId ID виртуальной машины
     */
    public void deleteStatusesForVirtualMachine(UUID virtualMachineId) {
        if (!virtualMachineRepository.existsById(virtualMachineId)) {
            throw new IllegalArgumentException("Virtual machine not found");
        }

        deploymentStatusRepository.deleteByVirtualMachineId(virtualMachineId);
    }

    /**
     * Удаляет все записи для указанного сервиса.
     *
     * @param serviceId ID сервиса
     */
    public void deleteStatusesForService(UUID serviceId) {
        if (!vulnerableServiceRepository.existsById(serviceId)) {
            throw new IllegalArgumentException("Service not found");
        }

        deploymentStatusRepository.deleteByVulnerableServiceId(serviceId);
    }

    /**
     * Инициализирует статус деплоя для каждой пары виртуальная машина-сервис.
     */
    public void initializeStatusesForAllCombinations() {
        var virtualMachines = virtualMachineRepository.findAll();
        var services = vulnerableServiceRepository.findAll();

        virtualMachines.forEach(vm ->
                services.forEach(service ->
                        mapAndSaveIfNotExists(vm.getId(), service.getId())
                )
        );
    }

    /**
     * Метод, который устанавливает статус PENDING для всех DeploymentStatusEntity.
     */
    public void setAllStatusesToPending() {
        var deploymentStatuses = deploymentStatusRepository.findAll();

        deploymentStatuses.forEach(status -> {
            status.setDeploymentStatus(DeploymentStatus.PENDING);
            status.setUpdatedAt(LocalDateTime.now());
            deploymentStatusRepository.save(status);
        });
    }
}
