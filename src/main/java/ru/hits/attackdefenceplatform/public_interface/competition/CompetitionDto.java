package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;

import java.time.LocalDateTime;

public record CompetitionDto(
        String name,
        CompetitionStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer totalRounds,
        Integer roundDurationMinutes,
        Integer currentRound,
        Integer flagSendCost,
        Integer flagLostCost,
        String rules
) {}
