package ru.hits.attackdefenceplatform.core.service_status.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.Optional;
import java.util.UUID;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatusEntity, UUID> {
    Optional<ServiceStatusEntity> findByServiceAndTeam(VulnerableServiceEntity service, TeamEntity team);
}
