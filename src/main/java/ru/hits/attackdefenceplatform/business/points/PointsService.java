package ru.hits.attackdefenceplatform.business.points;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.business.CompetitionContext;
import ru.hits.attackdefenceplatform.business.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.business.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.business.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.FlagPointsForServiceDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointsService {
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final CompetitionContext competitionContext;
    private final PointsCounterStrategyFactory pointsCounterStrategyFactory;

    public Double calculateTeamFlagPoints(TeamEntity team) {
        var competition = competitionContext.getCurrent();

        var strategy = pointsCounterStrategyFactory.getStrategy(competition.getCompetitionMode());
        return strategy.getTeamPoints(team.getId());
    }

    public FlagPointsForServiceDto getFlagPointsForServiceAndTeam(TeamEntity team, VulnerableServiceEntity service) {
        var competition = competitionContext.getCurrent();
        long plusPoints = flagSubmissionRepository.countByTeamAndFlag_VulnerableService(team, service)
                * competition.getFlagSendCost();

        long minusPoints = flagSubmissionRepository.countByFlag_FlagOwnerAndFlag_VulnerableService(team, service)
                * competition.getFlagLostCost();

        return new FlagPointsForServiceDto(plusPoints, minusPoints);
    }
}
