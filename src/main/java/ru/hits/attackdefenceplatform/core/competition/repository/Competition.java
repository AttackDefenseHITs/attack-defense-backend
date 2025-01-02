package ru.hits.attackdefenceplatform.core.competition.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "competitions")
@Data
public class Competition {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private CompetitionStatus status;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer durationMinutes;

    @Lob
    private String rules;
}
