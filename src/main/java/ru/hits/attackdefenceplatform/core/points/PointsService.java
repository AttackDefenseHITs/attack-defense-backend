package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.FlagPointsForServiceDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointsService {
    private final TeamMemberRepository teamMemberRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final CompetitionService competitionService;
    private final SlaService slaService;

    public Double calculateTeamFlagPoints(TeamEntity team) {
        var competitionDto = competitionService.getCompetitionDto();

        double totalPoints = teamMemberRepository.findByTeam(team).stream()
                .mapToDouble(member -> member.getPoints() != null ? member.getPoints() : 0)
                .sum();

        long stolenFlags = flagSubmissionRepository.countByFlag_FlagOwner(team);
        double stolenPoints = stolenFlags * competitionDto.flagLostCost();

        double netPoints = totalPoints - stolenPoints;
        if (netPoints < 0) {
            return roundToThreeDecimals(netPoints);
        }

        double result = netPoints * slaService.getTeamSla(team);
        return roundToThreeDecimals(result);
    }

    private Double roundToThreeDecimals(double value) {
        return new BigDecimal(value)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public FlagPointsForServiceDto getFlagPointsForServiceAndTeam(TeamEntity team, VulnerableServiceEntity service) {
        var competitionDto = competitionService.getCompetitionDto();
        long plusPoints = flagSubmissionRepository.countByTeamAndFlag_VulnerableService(team, service)
                * competitionDto.flagSendCost();

        long minusPoints = flagSubmissionRepository.countByFlag_FlagOwnerAndFlag_VulnerableService(team, service)
                * competitionDto.flagLostCost();

        return new FlagPointsForServiceDto(plusPoints, minusPoints);
    }
}
