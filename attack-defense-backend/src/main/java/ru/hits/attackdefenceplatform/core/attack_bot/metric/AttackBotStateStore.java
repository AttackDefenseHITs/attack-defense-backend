package ru.hits.attackdefenceplatform.core.attack_bot.metric;

import java.util.UUID;

public interface AttackBotStateStore {
    long getLastAttackRound(UUID teamId);
    void updateLastAttackRound(UUID teamId, long round);
}
