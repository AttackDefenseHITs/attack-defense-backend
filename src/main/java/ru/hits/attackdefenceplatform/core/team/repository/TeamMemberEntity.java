package ru.hits.attackdefenceplatform.core.team.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;

import java.util.UUID;

@Entity
@Table(name = "team_members")
@Data
public class TeamMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;
}
