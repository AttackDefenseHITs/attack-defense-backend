package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.public_interface.attack_bot.AttackBotSettingsDto;

import java.time.LocalDateTime;

public record UpdateCompetitionRequest(
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer totalRounds,
        Integer roundDurationMinutes,
        Integer flagSendCost,
        Integer flagLostCost,
        String rules,
        AttackBotSettingsDto attackBotSettings
) {
}
