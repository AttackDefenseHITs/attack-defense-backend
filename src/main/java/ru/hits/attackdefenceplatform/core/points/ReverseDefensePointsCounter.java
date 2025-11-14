package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReverseDefensePointsCounter extends PointsCounterStrategy {
    @Override
    public Double getTeamPoints(UUID teamId) {
        return 30000.0;
    }
}
