package ru.hits.attackdefenceplatform.modules.service_status;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.modules.service_status.repository.ServiceStatusRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlaService {
    private final ServiceStatusRepository serviceStatusRepository;
    private final static Double DEFAULT_SLA = 100.0;

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
}
