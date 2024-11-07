package ru.hits.attackdefenceplatform.core.flag.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;

@Entity
@Table(name = "flags")
@Data
public class Flag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity flagOwner;

    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
