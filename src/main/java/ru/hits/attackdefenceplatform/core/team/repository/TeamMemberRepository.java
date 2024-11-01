package ru.hits.attackdefenceplatform.core.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, UUID> {
    long countByTeam(TeamEntity team);

    Optional<TeamMemberEntity> findByUserAndTeam(UserEntity user, TeamEntity team);
}
