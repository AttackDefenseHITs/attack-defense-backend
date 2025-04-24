package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;

@Service
@RequiredArgsConstructor
public class SlaService {
    private final ServiceStatusRepository serviceStatusRepository;
    private final static Double DEFAULT_SLA = 100.0;

    public Double getTeamSla(TeamEntity team) {
        var teamStatuses = serviceStatusRepository.findByTeam(team);

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
        var result = totalSla / teamStatuses.size() / DEFAULT_SLA;

        return result;
    }
}
