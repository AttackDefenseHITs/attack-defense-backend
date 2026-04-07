package ru.hits.attackdefenceplatform.core.points.sla.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SlaRoundSnapshotService {

    private final ServiceStatusRepository serviceStatusRepository;
    private final TeamRepository teamRepository;
    private final TeamServiceSlaSnapshotStore snapshotStore;

    public void captureFinishedRound(long roundNumber) {
        var teams = teamRepository.findAllByIsSystemFalse();

        for (var team : teams) {
            var snapshots = serviceStatusRepository.findByTeamId(team.getId()).stream()
                    .map(status -> new TeamServiceSlaRoundSnapshot(
                            team.getId(),
                            status.getService().getId(),
                            roundNumber,
                            status.getTotalDuration(),
                            status.getTotalOkDuration(),
                            LocalDateTime.now()
                    ))
                    .toList();

            snapshotStore.saveAll(roundNumber, team.getId(), snapshots);
        }
    }
}
