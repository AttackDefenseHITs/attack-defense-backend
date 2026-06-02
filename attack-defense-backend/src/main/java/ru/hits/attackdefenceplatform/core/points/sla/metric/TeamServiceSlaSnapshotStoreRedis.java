package ru.hits.attackdefenceplatform.core.points.sla.metric;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceSlaSnapshotStoreRedis implements TeamServiceSlaSnapshotStore {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Override
    public void saveAll(long roundNumber, UUID teamId, List<TeamServiceSlaRoundSnapshot> snapshots) {
        try {
            String key = TeamServiceSlaSnapshotKeys.teamRound(teamId, roundNumber);
            String value = objectMapper.writeValueAsString(snapshots);
            redis.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save SLA snapshots", e);
        }
    }

    @Override
    public List<TeamServiceSlaRoundSnapshot> getByTeamAndRound(UUID teamId, long roundNumber) {
        try {
            String key = TeamServiceSlaSnapshotKeys.teamRound(teamId, roundNumber);
            String value = redis.opsForValue().get(key);

            if (value == null || value.isBlank()) {
                return List.of();
            }

            return objectMapper.readValue(
                    value,
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read SLA snapshots", e);
        }
    }
}
