package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.points.sla.RoundStatusScoreService;
import ru.hits.attackdefenceplatform.util.NumberUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReverseDefensePointsCounter implements PointsCounterStrategy {
    private static final double REVERSE_DEFENSE_POINTS = 50000.0;

    private final RoundStatusScoreService roundStatusScoreService;
    private final CompetitionContext competitionContext;

    @Override
    public Double getTeamPoints(UUID teamId) {
        int currentRound = competitionContext.getCurrent().getCurrentRound();
        int totalRounds = competitionContext.getCurrent().getTotalRounds();

        double roundBasePoints = REVERSE_DEFENSE_POINTS / totalRounds;
        double totalPenalty = 0.0;

        for (int round = 0; round <= currentRound; round++) {
            double roundScore = roundStatusScoreService.getTeamRoundScore(teamId, round);
            totalPenalty += roundBasePoints * (1.0 - roundScore);
        }

        double result = REVERSE_DEFENSE_POINTS - totalPenalty;
        return NumberUtils.roundToThreeDecimals(Math.max(result, 0.0));
    }
}
