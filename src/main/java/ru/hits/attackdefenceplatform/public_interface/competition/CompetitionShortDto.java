package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;

import java.time.LocalDateTime;

public record CompetitionShortDto(
        String name,
        CompetitionStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer roundDurationMinutes,
        Integer currentRound,
        String rules,
        String competitionMode
) {}
