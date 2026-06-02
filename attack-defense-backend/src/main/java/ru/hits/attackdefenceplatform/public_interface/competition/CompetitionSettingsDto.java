package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.public_interface.attack_bot.AttackBotSettingsDto;

import java.time.LocalDateTime;

public record CompetitionSettingsDto(
        String name,
        CompetitionStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer totalRounds,
        Integer roundDurationMinutes,
        Integer currentRound,
        Integer flagSendCost,
        Integer flagLostCost,
        String rules,
        String competitionMode,
        AttackBotSettingsDto attackBotSettings,
        String repoUrl
) {}

