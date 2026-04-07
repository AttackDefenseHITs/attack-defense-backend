package ru.hits.attackdefenceplatform.core.dashboard.metric;

import ru.hits.attackdefenceplatform.core.dashboard.model.TeamRoundSnapshot;

import java.util.List;

public interface DashboardRoundSnapshotStore {
    void saveRoundSnapshot(long roundNumber, List<TeamRoundSnapshot> snapshots);
    List<TeamRoundSnapshot> getAllSnapshots();
}
