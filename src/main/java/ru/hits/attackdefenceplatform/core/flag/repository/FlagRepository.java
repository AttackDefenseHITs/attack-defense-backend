package ru.hits.attackdefenceplatform.core.flag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlagRepository extends JpaRepository<FlagEntity, UUID> {
    List<FlagEntity> findByVulnerableService(VulnerableServiceEntity vulnerableService);

    List<FlagEntity> findByFlagOwner(TeamEntity flagOwner);

    Optional<FlagEntity> findByValue(String value);

    @Query("SELECT f FROM FlagEntity f WHERE f.vulnerableService.id = :serviceId AND f.flagOwner.id = :teamId")
    List<FlagEntity> findFlagsByServiceAndTeam(@Param("serviceId") UUID serviceId, @Param("teamId") UUID teamId);
}
