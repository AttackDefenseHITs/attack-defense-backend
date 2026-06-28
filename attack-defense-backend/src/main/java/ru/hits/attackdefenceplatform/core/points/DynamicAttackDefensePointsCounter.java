package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.points.sla.SlaService;
import ru.hits.attackdefenceplatform.util.NumberUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DynamicAttackDefensePointsCounter implements PointsCounterStrategy {
    private final CompetitionContext competitionContext;

    private final FlagSubmissionRepository flagSubmissionRepository;
    private final SlaService slaService;

    @Override
    public Double getTeamPoints(UUID teamId) {
        var competition = competitionContext.getCurrent();

        double attackPoints = flagSubmissionRepository
                .findAllByTeamIdAndIsCorrectTrue(teamId).stream()
                .mapToDouble(FlagSubmissionEntity::getPointsAwarded)
                .sum();
        double slaScore = slaService.getTeamSla(teamId);
        long lostFlags = flagSubmissionRepository
                .countByFlag_FlagOwner_Id(teamId);

        double lostPenalty = lostFlags * competition.getFlagLostCost();
        double result = attackPoints - lostPenalty;

        if (result > 0) {
            result = result * slaScore;
        }

        return NumberUtils.roundToThreeDecimals(result);
    }
}
