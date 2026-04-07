package ru.hits.attackdefenceplatform.core.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.dashboard.metric.DashboardRoundSnapshotStore;
import ru.hits.attackdefenceplatform.core.dashboard.model.TeamRoundSnapshot;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoundSnapshotDashboardServiceImpl implements DashboardService {

    private final DashboardRoundSnapshotStore snapshotStore;

    @Override
    public List<TeamScoreChangeDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId) {
        return snapshotStore.getAllSnapshots().stream()
                .filter(s -> teamId == null || s.teamId().equals(teamId))
                .sorted(Comparator.comparing(TeamRoundSnapshot::snapshotTime))
                .map(s -> new TeamScoreChangeDto(
                        s.teamName(),
                        s.snapshotTime(),
                        0.0,
                        s.totalPoints(),
                        s.teamColor()
                ))
                .toList();
    }
}
