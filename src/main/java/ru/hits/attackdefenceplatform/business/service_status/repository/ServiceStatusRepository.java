package ru.hits.attackdefenceplatform.business.service_status.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.attackdefenceplatform.business.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.business.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatusEntity, UUID> {
    Optional<ServiceStatusEntity> findByServiceAndTeam(VulnerableServiceEntity service, TeamEntity team);
    List<ServiceStatusEntity> findByTeamId(UUID teamId);
}
