package ru.hits.attackdefenceplatform.core.attack_bot.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttackBotStateStoreRedis implements AttackBotStateStore {

    private final StringRedisTemplate redis;

    @Override
    public long getLastAttackRound(UUID teamId) {
        String key = AttackBotMetricKeys.lastAttackRound(teamId);
        String value = redis.opsForValue().get(key);
        return (value == null) ? -1L : Long.parseLong(value);
    }

    @Override
    public void updateLastAttackRound(UUID teamId, long round) {
        String key = AttackBotMetricKeys.lastAttackRound(teamId);
        redis.opsForValue().set(key, Long.toString(round));
    }
}
