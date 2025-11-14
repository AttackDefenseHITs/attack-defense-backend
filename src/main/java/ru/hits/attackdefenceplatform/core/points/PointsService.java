package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.service_status.SlaService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.FlagPointsForServiceDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
