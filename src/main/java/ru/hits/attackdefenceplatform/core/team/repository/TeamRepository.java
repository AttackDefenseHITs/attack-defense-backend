package ru.hits.attackdefenceplatform.core.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hits.attackdefenceplatform.core.team.repository.model.TeamPointsDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {
    @Query("""
    SELECT new ru.hits.attackdefenceplatform.core.team.repository.model.TeamPointsDto(team.id, COALESCE(SUM(member.points), 0))
    FROM TeamEntity team
    LEFT JOIN team.members member
    GROUP BY team.id
    ORDER BY COALESCE(SUM(member.points), 0) DESC
""")
    List<TeamPointsDto> getTeamPointsRanked();

    @Query("""
    SELECT new ru.hits.attackdefenceplatform.core.team.repository.model.TeamPointsDto(team.id, COALESCE(SUM(member.points), 0))
    FROM TeamEntity team
    LEFT JOIN team.members member
    WHERE team.id = :teamId
    GROUP BY team.id
""")
    Optional<TeamPointsDto> getTeamPointsById(@Param("teamId") UUID teamId);
}
