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
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.UUID;

@Entity
@Table(name = "flags")
@Data
public class FlagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity flagOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vulnerable_service_id", nullable = false)
    private VulnerableServiceEntity vulnerableService;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
