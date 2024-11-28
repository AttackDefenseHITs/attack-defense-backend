package ru.hits.attackdefenceplatform.core.virtual_machine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;

import java.util.List;
import java.util.UUID;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachineEntity, UUID> {
    List<VirtualMachineEntity> findAllByTeam(TeamEntity team);
}
