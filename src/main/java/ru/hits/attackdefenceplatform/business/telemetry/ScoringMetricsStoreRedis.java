package ru.hits.attackdefenceplatform.business.telemetry;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScoringMetricsStoreRedis implements ScoringMetricsStore {

    private final StringRedisTemplate redis;

    @Override
    public void recordFlagCapture(UUID teamId, UUID serviceId, int round) {
        String lastFlagKey = MetricKeys.lastFlagRound(serviceId, teamId);
        redis.opsForValue().set(lastFlagKey, String.valueOf(round));

        String capturedKey = MetricKeys.capturedTeams(serviceId, round);
        redis.opsForSet().add(capturedKey, teamId.toString());
    }

    @Override
    public int getRoundsWithoutFlag(UUID teamId, UUID serviceId, int currentRound) {
        String key = MetricKeys.lastFlagRound(serviceId, teamId);

        String lastRound = redis.opsForValue().get(key);
        if (lastRound == null) {
            return currentRound;
        }

        return currentRound - Integer.parseInt(lastRound);
    }

    @Override
    public int getCaptureCount(UUID serviceId, int round) {
        String key = MetricKeys.capturedTeams(serviceId, round);
        Long size = redis.opsForSet().size(key);
        return size == null ? 0 : size.intValue();
    }

    @Override
    public void clearAll() {
        Set<String> keys = redis.keys("LAST_FLAG_ROUND:*");
        if (!keys.isEmpty()) redis.delete(keys);

        keys = redis.keys("CAPTURED_TEAMS:*");
        if (!keys.isEmpty()) redis.delete(keys);
    }
}
