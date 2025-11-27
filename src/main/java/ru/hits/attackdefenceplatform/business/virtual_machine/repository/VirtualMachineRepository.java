package ru.hits.attackdefenceplatform.business.virtual_machine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.attackdefenceplatform.business.team.repository.TeamEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachineEntity, UUID> {
    List<VirtualMachineEntity> findAllByTeam(TeamEntity team);
    Optional<VirtualMachineEntity> findByIpAddress(String ipAddress);
}
