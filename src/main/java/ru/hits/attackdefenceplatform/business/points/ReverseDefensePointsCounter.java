package ru.hits.attackdefenceplatform.business.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.business.service_status.SlaService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReverseDefensePointsCounter implements PointsCounterStrategy {
    private static final double REVERSE_DEFENSE_POINTS = 30000.0;

    private final SlaService slaService;

    @Override
    public Double getTeamPoints(UUID teamId) {
        return REVERSE_DEFENSE_POINTS * slaService.getTeamSla(teamId);
    }
}
