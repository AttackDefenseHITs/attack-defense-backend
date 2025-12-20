package ru.hits.attackdefenceplatform.core.deploy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeploymentStatusRepository extends JpaRepository<DeploymentStatusEntity, UUID> {

    /**
     * Проверяет, существует ли запись для данной пары виртуальной машины и сервиса.
     *
     * @param virtualMachineId    ID виртуальной машины
     * @param vulnerableServiceId ID сервиса
     * @return true, если запись существует, иначе false
     */
    boolean existsByVirtualMachineIdAndVulnerableServiceId(UUID virtualMachineId, UUID vulnerableServiceId);

    /**
     * Находит запись по ID виртуальной машины и ID сервиса.
     *
     * @param virtualMachineId    ID виртуальной машины
     * @param vulnerableServiceId ID сервиса
     * @return Optional с найденной записью или пустым значением
     */
    Optional<DeploymentStatusEntity> findByVirtualMachineIdAndVulnerableServiceId(UUID virtualMachineId, UUID vulnerableServiceId);

    /**
     * Удаляет все записи для указанной виртуальной машины.
     *
     * @param virtualMachineId ID виртуальной машины
     */
    void deleteByVirtualMachineId(UUID virtualMachineId);

    /**
     * Удаляет все записи для указанного сервиса.
     *
     * @param vulnerableServiceId ID сервиса
     */
    void deleteByVulnerableServiceId(UUID vulnerableServiceId);
}
