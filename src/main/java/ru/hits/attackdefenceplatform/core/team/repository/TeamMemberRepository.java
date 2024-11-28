package ru.hits.attackdefenceplatform.core.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
