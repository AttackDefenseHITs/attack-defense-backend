package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.FlagCostProperties;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.FlagPointsForServiceDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointsService {
    private final TeamMemberRepository teamMemberRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final FlagCostProperties flagCostProperties;

    public Double calculateTeamFlagPoints(TeamEntity team) {
        double totalPoints = teamMemberRepository.findByTeam(team).stream()
                .mapToDouble(member -> member.getPoints() != null ? member.getPoints() : 0)
                .sum();

        long stolenFlags = flagSubmissionRepository.countByFlag_FlagOwner(team);
        double stolenPoints = stolenFlags * flagCostProperties.getFlagLost();

        return totalPoints - stolenPoints;
    }

    public FlagPointsForServiceDto getFlagPointsForServiceAndTeam(TeamEntity team, VulnerableServiceEntity service) {
        long plusPoints = flagSubmissionRepository.countByTeamAndFlag_VulnerableService(team, service)
                * flagCostProperties.getFlagCost();

        long minusPoints = flagSubmissionRepository.countByFlag_FlagOwnerAndFlag_VulnerableService(team, service)
                * flagCostProperties.getFlagLost();

        return new FlagPointsForServiceDto(plusPoints, minusPoints);
    }
}
