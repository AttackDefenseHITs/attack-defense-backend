package ru.hits.attackdefenceplatform.core.telemetry;

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
        String firstFlagKey = MetricKeys.firstFlagRound(serviceId, teamId);
        redis.opsForValue().setIfAbsent(firstFlagKey, String.valueOf(round));

        String capturedKey = MetricKeys.capturedTeams(serviceId, round);
        redis.opsForSet().add(capturedKey, teamId.toString());
    }

    @Override
    public int getRoundsUntilFirstFlag(UUID teamId, UUID serviceId, int currentRound) {
        String key = MetricKeys.firstFlagRound(serviceId, teamId);

        String firstRound = redis.opsForValue().get(key);
        if (firstRound == null) {
            return currentRound;
        }

        return Integer.parseInt(firstRound);
    }

    @Override
    public int getCaptureCount(UUID serviceId, int round) {
        String key = MetricKeys.capturedTeams(serviceId, round);
        Long size = redis.opsForSet().size(key);
        return size == null ? 0 : size.intValue();
    }

    @Override
    public void clearAll() {
        Set<String> keys = redis.keys("FIRST_FLAG_ROUND:*");
        if (!keys.isEmpty()) redis.delete(keys);

        keys = redis.keys("LAST_FLAG_ROUND:*");
        if (!keys.isEmpty()) redis.delete(keys);

        keys = redis.keys("CAPTURED_TEAMS:*");
        if (!keys.isEmpty()) redis.delete(keys);
    }
}
