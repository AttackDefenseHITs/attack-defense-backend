package ru.hits.attackdefenceplatform.business.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.business.CompetitionContext;
import ru.hits.attackdefenceplatform.business.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.business.service_status.SlaService;
import ru.hits.attackdefenceplatform.business.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.common.util.NumberUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttackDefensePointsCounter implements PointsCounterStrategy {
    private final CompetitionContext competitionContext;

    private final TeamRepository teamRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final SlaService slaService;

    @Override
    public Double getTeamPoints(UUID teamId) {
        var competition = competitionContext.getCurrent();
        var teamPointsDto = teamRepository.getTeamPointsById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        double totalPoints = teamPointsDto.points();
        long stolenFlags = flagSubmissionRepository.countByFlag_FlagOwner_Id(teamId);
        double stolenPoints = stolenFlags * competition.getFlagLostCost();

        double netPoints = totalPoints - stolenPoints;
        if (netPoints < 0) {
            return NumberUtils.roundToThreeDecimals(netPoints);
        }

        double result = netPoints * slaService.getTeamSla(teamId);
        return NumberUtils.roundToThreeDecimals(result);
    }
}
