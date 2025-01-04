package ru.hits.attackdefenceplatform.core.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, UUID> {
    long countByTeam(TeamEntity team);

    Optional<TeamMemberEntity> findByUserAndTeam(UserEntity user, TeamEntity team);

    boolean existsByUser(UserEntity user);

    List<TeamMemberEntity> findByTeam(TeamEntity team);

    Optional<TeamMemberEntity> findByUser(UserEntity user);

    boolean existsByUserAndTeam(UserEntity user, TeamEntity team);

    @Query("SELECT t.user.id FROM TeamMemberEntity t")
    List<UUID> findAllUserIds();

    @Query("SELECT t FROM TeamMemberEntity t WHERE t.user.id = :userId AND t.team.id = :teamId")
    Optional<TeamMemberEntity> findByUserIdAndTeamId(@Param("userId") UUID userId, @Param("teamId") UUID teamId);
}

