package ru.hits.attackdefenceplatform.core.attack_bot.metric;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class AttackBotMetricKeys {
    public static String lastAttackRound(UUID teamId) {
        return "ATTACK_BOT_LAST_ATTACK_ROUND:%s".formatted(teamId);
    }
}
