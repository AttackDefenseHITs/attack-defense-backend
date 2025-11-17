package ru.hits.attackdefenceplatform.core.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.List;
import java.util.UUID;

public interface FlagSubmissionRepository extends JpaRepository<FlagSubmissionEntity, UUID>, JpaSpecificationExecutor<FlagSubmissionEntity> {
    long countByTeamAndFlag_VulnerableService(TeamEntity team, VulnerableServiceEntity service);
    long countByFlag_FlagOwnerAndFlag_VulnerableService(TeamEntity team, VulnerableServiceEntity service);
    long countByFlag_FlagOwner_Id(UUID teamId);
    List<FlagSubmissionEntity> findAllByTeamIdAndIsCorrectTrue(UUID teamId);
}

