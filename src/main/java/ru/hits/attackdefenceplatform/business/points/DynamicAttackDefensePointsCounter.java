package ru.hits.attackdefenceplatform.business.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.business.CompetitionContext;
import ru.hits.attackdefenceplatform.business.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.business.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.business.service_status.SlaService;
import ru.hits.attackdefenceplatform.common.util.NumberUtils;

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
        double result = (attackPoints - lostPenalty) * slaScore;

        return NumberUtils.roundToThreeDecimals(result);
    }
}
