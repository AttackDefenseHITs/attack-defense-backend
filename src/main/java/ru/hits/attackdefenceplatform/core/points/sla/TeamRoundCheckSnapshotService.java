package ru.hits.attackdefenceplatform.core.points.sla;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.points.sla.metric.TeamServiceSlaRoundSnapshot;
import ru.hits.attackdefenceplatform.core.points.sla.metric.TeamServiceSlaSnapshotStore;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class TeamRoundCheckSnapshotService {

    private final ServiceStatusRepository serviceStatusRepository;
    private final TeamRepository teamRepository;
    private final TeamServiceSlaSnapshotStore snapshotStore;

    public void captureFinishedRound(long roundNumber) {
        var teams = teamRepository.findAllByIsSystemFalse();

        for (var team : teams) {
            var snapshots = serviceStatusRepository.findByTeamId(team.getId()).stream()
                    .map(status -> new TeamServiceSlaRoundSnapshot(
                            status.getTeam().getId(),
                            status.getService().getId(),
                            roundNumber,
                            status.getLastStatus(),
                            LocalDateTime.now(ZoneOffset.UTC)
                    ))
                    .toList();

            snapshotStore.saveAll(roundNumber, team.getId(), snapshots);
        }
    }
}
