package ru.hits.attackdefenceplatform.core.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {

    @Query("SELECT t FROM TeamEntity t LEFT JOIN FETCH t.members WHERE t.id = :teamId")
    Optional<TeamEntity> findByIdWithMembers(@Param("teamId") UUID teamId);

    @Query("SELECT t FROM TeamEntity t LEFT JOIN FETCH t.members")
    List<TeamEntity> findAllWithMembers();
}
