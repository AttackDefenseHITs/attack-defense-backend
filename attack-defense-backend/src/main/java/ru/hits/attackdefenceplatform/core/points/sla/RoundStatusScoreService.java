package ru.hits.attackdefenceplatform.core.points.sla;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.points.sla.metric.TeamServiceSlaSnapshotStore;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoundStatusScoreService {

    private final TeamServiceSlaSnapshotStore snapshotStore;

    public Double getTeamRoundScore(UUID teamId, long roundNumber) {
        var snapshots = snapshotStore.getByTeamAndRound(teamId, roundNumber);

        if (snapshots.isEmpty()) {
            return 1.0;
        }

        return snapshots.stream()
                .mapToDouble(snapshot -> mapStatusToScore(snapshot.getStatus()))
                .average()
                .orElse(1.0);
    }

    private double mapStatusToScore(CheckerResult status) {
        return switch (status) {
            case OK -> 1.0;
            case MUMBLE -> 0.5;
            case CORRUPT -> 0.25;
            case DOWN -> 0.0;
            default -> 0.0;
        };
    }
}
