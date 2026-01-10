package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.configuration.properties.CompetitionDynamicScoringProperties;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.hint.HintPenaltyService;
import ru.hits.attackdefenceplatform.core.telemetry.ScoringMetricsStore;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DynamicPointsCalculator {
    private final ScoringMetricsStore metrics;
    private final CompetitionDynamicScoringProperties properties;
    private final CompetitionContext competitionContext;
    private final HintPenaltyService hintPenaltyService;

    public double calculate(UUID teamId, UUID serviceId, int round) {

        int t = metrics.getRoundsWithoutFlag(teamId, serviceId, round);
        int n = metrics.getCaptureCount(serviceId, round);

        double b = competitionContext.getCurrent().getFlagSendCost();
        double alpha = properties.getAlpha();
        double gamma = properties.getGamma();

        return b * Math.pow(1 + alpha, t) * (1.0 / (1 + gamma + n)) * hintPenaltyService.getHintsMultiplier(teamId, serviceId);
    }
}
