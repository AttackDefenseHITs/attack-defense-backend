package ru.hits.attackdefenceplatform.core.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {

    @Query("SELECT new ru.hits.attackdefenceplatform.core.team.repository.TeamPointsDto(t.id, SUM(t.points)) FROM TeamMemberEntity t WHERE t.team.id = :teamId GROUP BY t.team.id ORDER BY SUM(t.points) DESC")
    List<TeamPointsDto> findAllTeamsWithPoints();
}
