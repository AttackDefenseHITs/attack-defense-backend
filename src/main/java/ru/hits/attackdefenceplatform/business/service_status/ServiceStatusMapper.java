package ru.hits.attackdefenceplatform.business.service_status;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.points.PointsService;
import ru.hits.attackdefenceplatform.business.service_status.repository.ServiceStatusEntity;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusSummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceStatusMapper {
    private final static Double DEFAULT_SLA = 100.0;
    private final PointsService pointsService;

    public Map<String, ServiceStatusSummary> mapStatusesToServiceSummaries(List<ServiceStatusEntity> statusesForTeam) {
        return statusesForTeam.stream()
                .collect(Collectors.toMap(
                        status -> status.getService().getName(),
                        status -> {

                            long totalDuration = status.getTotalOkDuration() + status.getTotalMumbleDuration()
                                    + status.getTotalCorruptDuration() + status.getTotalDownDuration();

                            double sla = totalDuration == 0
                                    ? DEFAULT_SLA
                                    : (status.getTotalOkDuration() * DEFAULT_SLA / totalDuration);

                            var flagPoints = pointsService
                                    .getFlagPointsForServiceAndTeam(status.getTeam(), status.getService());

                            return new ServiceStatusSummary(
                                    status.getService().getId(),
                                    String.format("%.2f%%", sla),
                                    flagPoints,
                                    status.getLastStatus()
                            );
                        }
                ));
    }
}
