package ru.hits.attackdefenceplatform.core.points.sla.metric;

import java.util.List;
import java.util.UUID;

public interface TeamServiceSlaSnapshotStore {
    void saveAll(long roundNumber, UUID teamId, List<TeamServiceSlaRoundSnapshot> snapshots);
    List<TeamServiceSlaRoundSnapshot> getByTeamAndRound(UUID teamId, long roundNumber);
}