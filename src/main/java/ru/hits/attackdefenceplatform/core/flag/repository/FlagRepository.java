package ru.hits.attackdefenceplatform.core.flag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.vulnerable.VulnerableServiceEntity;

import java.util.List;
import java.util.UUID;

public interface FlagRepository extends JpaRepository<FlagEntity, UUID> {
    boolean existsByVulnerableServiceAndFlagNumber(VulnerableServiceEntity vulnerableService, Integer flagNumber);

    List<FlagEntity> findByVulnerableService(VulnerableServiceEntity vulnerableService);

    List<FlagEntity> findByFlagOwner(TeamEntity flagOwner);
}
