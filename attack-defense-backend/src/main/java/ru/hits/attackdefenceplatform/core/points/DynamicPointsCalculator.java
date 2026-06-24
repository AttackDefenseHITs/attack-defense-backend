package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.configuration.properties.CompetitionDynamicScoringProperties;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.hint.HintPenaltyService;
import ru.hits.attackdefenceplatform.core.telemetry.ScoringMetricsStore;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicPointsCalculator {
    private final ScoringMetricsStore metrics;
    private final CompetitionDynamicScoringProperties properties;
    private final CompetitionContext competitionContext;
    private final HintPenaltyService hintPenaltyService;

    public double calculate(UUID teamId, UUID serviceId, int round) {

        int t = metrics.getRoundsUntilFirstFlag(teamId, serviceId, round);
        int n = metrics.getCaptureCount(serviceId, round - 1);

        double b = competitionContext.getCurrent().getFlagSendCost();
        double alpha = properties.getAlpha();
        double gamma = properties.getGamma();
        double hintsMultiplier = hintPenaltyService.getHintsMultiplier(teamId, serviceId);
        double points = b * Math.pow(1 + alpha, t) * (1.0 / (1 + gamma + n)) * hintsMultiplier;

        log.info(
                "Dynamic points calculated: teamId={}, serviceId={}, round={}, previousRound={}, b={}, alpha={}, gamma={}, t={}, n={}, hintsMultiplier={}, points={}",
                teamId,
                serviceId,
                round,
                round - 1,
                b,
                alpha,
                gamma,
                t,
                n,
                hintsMultiplier,
                points
        );

        return points;
    }
}
