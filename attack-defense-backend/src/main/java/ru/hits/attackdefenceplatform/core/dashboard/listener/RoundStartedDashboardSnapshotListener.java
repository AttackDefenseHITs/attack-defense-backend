package ru.hits.attackdefenceplatform.core.dashboard.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.dashboard.metric.DashboardRoundSnapshotStore;
import ru.hits.attackdefenceplatform.core.dashboard.model.TeamRoundSnapshot;
import ru.hits.attackdefenceplatform.core.points.PointsService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.publisher.RoundStartedEvent;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoundStartedDashboardSnapshotListener {

    private final TeamRepository teamRepository;
    private final PointsService pointsService;
    private final DashboardRoundSnapshotStore snapshotStore;
    private final EventBus eventBus;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRoundStarted(RoundStartedEvent event) {
        List<TeamEntity> teams = teamRepository.findAllByIsSystemFalse();

        List<TeamRoundSnapshot> snapshots = teams.stream()
                .map(team -> new TeamRoundSnapshot(
                        event.roundNumber(),
                        team.getId(),
                        team.getName(),
                        team.getColor(),
                        Timestamp.valueOf(event.startedAt()),
                        pointsService.calculateTeamFlagPoints(team)
                ))
                .toList();

        snapshotStore.saveRoundSnapshot(event.roundNumber(), snapshots);
    }
}
