package ru.hits.attackdefenceplatform.core.service_status.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.ZoneOffset;
import java.util.UUID;

import jakarta.persistence.*;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_statuses")
@Data
public class ServiceStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private VulnerableServiceEntity service;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_status", nullable = false)
    private CheckerResult lastStatus;

    @Column(name = "last_changed", nullable = false)
    private LocalDateTime lastChanged;

    @Column(name = "total_ok_duration", nullable = false)
    private long totalOkDuration = 0;

    @Column(name = "total_mumble_duration", nullable = false)
    private long totalMumbleDuration = 0;

    @Column(name = "total_corrupt_duration", nullable = false)
    private long totalCorruptDuration = 0;

    @Column(name = "total_down_duration", nullable = false)
    private long totalDownDuration = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(ZoneOffset.UTC);

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Обновляет длительность текущего статуса в зависимости от CheckerResult.
     * @param result Новый результат проверки.
     */
    public void updateDuration(CheckerResult result) {
        var now = LocalDateTime.now(ZoneOffset.UTC);
        if (lastChanged != null) {
            long elapsedSeconds = java.time.Duration.between(lastChanged, now).getSeconds();

            switch (lastStatus) {
                case OK -> totalOkDuration += elapsedSeconds;
                case MUMBLE -> totalMumbleDuration += elapsedSeconds;
                case CORRUPT -> totalCorruptDuration += elapsedSeconds;
                case DOWN -> totalDownDuration += elapsedSeconds;
                default -> { }
            }
        }

        lastStatus = result;
        lastChanged = now;
    }

    public long getTotalDuration() {
        return totalOkDuration + totalMumbleDuration + totalCorruptDuration + totalDownDuration;
    }
}

