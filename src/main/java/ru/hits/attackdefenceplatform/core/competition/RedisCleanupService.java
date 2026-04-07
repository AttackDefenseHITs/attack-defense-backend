package ru.hits.attackdefenceplatform.core.competition;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCleanupService {

    private final StringRedisTemplate redis;

    public void clearAllGameData() {
        delete("TEAM_SERVICE_SLA_SNAPSHOT:*");
        delete("ATTACK_BOT_LAST_ATTACK_ROUND:*");
        delete("DASHBOARD_ROUND_SNAPSHOT:*");
    }

    private void delete(String pattern) {
        var keys = redis.keys(pattern);
        if (!keys.isEmpty()) {
            redis.delete(keys);
        }
    }
}
