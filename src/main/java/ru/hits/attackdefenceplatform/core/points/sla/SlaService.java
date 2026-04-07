package ru.hits.attackdefenceplatform.core.points.sla;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.points.sla.metric.TeamServiceSlaRoundSnapshot;
import ru.hits.attackdefenceplatform.core.points.sla.metric.TeamServiceSlaSnapshotStore;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlaService {
    private static final Double DEFAULT_SLA = 100.0;

    private final ServiceStatusRepository serviceStatusRepository;
    private final TeamServiceSlaSnapshotStore snapshotStore;

    public Double getTeamSla(UUID teamId) {
        var teamStatuses = serviceStatusRepository.findByTeamId(teamId);

        if (teamStatuses.isEmpty()) {
            return 1.0;
        }

        double totalSla = 0.0;

        for (var status : teamStatuses) {
            long totalDuration = status.getTotalDuration();
            double sla = totalDuration == 0
                    ? DEFAULT_SLA
                    : (status.getTotalOkDuration() * DEFAULT_SLA / totalDuration);

            totalSla += sla;
        }

        return totalSla / teamStatuses.size() / DEFAULT_SLA;
    }

    public Double getTeamSlaForRound(UUID teamId, long roundNumber) {
        var currentSnapshots = snapshotStore.getByTeamAndRound(teamId, roundNumber);
        if (currentSnapshots.isEmpty()) {
            return 1.0;
        }

        if (roundNumber == 0) {
            return calculateFromAbsolute(currentSnapshots);
        }

        var previousSnapshots = snapshotStore.getByTeamAndRound(teamId, roundNumber - 1);
        if (previousSnapshots.isEmpty()) {
            return calculateFromAbsolute(currentSnapshots);
        }

        return calculateFromDiff(currentSnapshots, previousSnapshots);
    }

    private Double calculateFromAbsolute(List<TeamServiceSlaRoundSnapshot> snapshots) {
        double totalSla = 0.0;

        for (var snapshot : snapshots) {
            long totalDuration = snapshot.getTotalDuration();
            double sla = totalDuration == 0
                    ? DEFAULT_SLA
                    : (snapshot.getTotalOkDuration() * DEFAULT_SLA / totalDuration);

            totalSla += sla;
        }

        return totalSla / snapshots.size() / DEFAULT_SLA;
    }

    private Double calculateFromDiff(
            List<TeamServiceSlaRoundSnapshot> currentSnapshots,
            List<TeamServiceSlaRoundSnapshot> previousSnapshots
    ) {
        Map<UUID, TeamServiceSlaRoundSnapshot> previousByServiceId = previousSnapshots.stream()
                .collect(Collectors.toMap(
                        TeamServiceSlaRoundSnapshot::getServiceId,
                        Function.identity()
                ));

        double totalSla = 0.0;
        int countedServices = 0;

        for (var current : currentSnapshots) {
            var previous = previousByServiceId.get(current.getServiceId());

            long roundTotalDuration = current.getTotalDuration();
            long roundOkDuration = current.getTotalOkDuration();

            if (previous != null) {
                roundTotalDuration -= previous.getTotalDuration();
                roundOkDuration -= previous.getTotalOkDuration();
            }

            if (roundTotalDuration < 0) {
                roundTotalDuration = 0;
            }
            if (roundOkDuration < 0) {
                roundOkDuration = 0;
            }

            double sla = roundTotalDuration == 0
                    ? DEFAULT_SLA
                    : (roundOkDuration * DEFAULT_SLA / roundTotalDuration);

            totalSla += sla;
            countedServices++;
        }

        if (countedServices == 0) {
            return 1.0;
        }

        return totalSla / countedServices / DEFAULT_SLA;
    }
}
