package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.CompetitionContext;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttackDefensePointsCounter implements PointsCounterStrategy {
    private final CompetitionContext competitionContext;

    @Override
    public Double getTeamPoints(UUID teamId) {
        return 0.0;
    }
}
