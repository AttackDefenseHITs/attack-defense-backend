package ru.hits.attackdefenceplatform.core.dashboard.metric;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.dashboard.model.TeamRoundSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DashboardRoundSnapshotStoreRedis implements DashboardRoundSnapshotStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Override
    public void saveRoundSnapshot(long roundNumber, List<TeamRoundSnapshot> snapshots) {
        try {
            String key = DashboardMetricKeys.roundSnapshot(roundNumber);
            String value = objectMapper.writeValueAsString(snapshots);
            redis.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save round snapshots", e);
        }
    }

    @Override
    public List<TeamRoundSnapshot> getAllSnapshots() {
        try {
            Set<String> keys = redis.keys("DASHBOARD_ROUND_SNAPSHOT:*");
            if (keys.isEmpty()) {
                return List.of();
            }

            List<TeamRoundSnapshot> result = new ArrayList<>();
            for (String key : keys) {
                String value = redis.opsForValue().get(key);
                if (value == null || value.isBlank()) {
                    continue;
                }

                List<TeamRoundSnapshot> snapshots = objectMapper.readValue(
                        value,
                        new TypeReference<>() {
                        }
                );
                result.addAll(snapshots);
            }

            result.sort(Comparator.comparing(TeamRoundSnapshot::snapshotTime));
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read round snapshots", e);
        }
    }
}
