package ru.hits.attackdefenceplatform.core.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.FlagCostProperties;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.dashboard.repository.spec.FlagSubmissionSpecifications;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final FlagSubmissionRepository flagSubmissionRepository;
    private final FlagCostProperties flagCostProperties;
    private final CompetitionService competitionService;
    private final TeamRepository teamRepository;

    @Override
    public List<TeamScoreChangeDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId) {
        Specification<FlagSubmissionEntity> spec = FlagSubmissionSpecifications.createSpecification(isCorrect, teamId);
        var submissions = flagSubmissionRepository.findAll(spec);
        return convertSubmissionsToDTO(submissions);
    }

    private List<TeamScoreChangeDto> convertSubmissionsToDTO(List<FlagSubmissionEntity> submissions) {
        Map<String, Integer> teamPointsMap = new HashMap<>();
        List<TeamScoreChangeDto> result = new ArrayList<>();

        for (FlagSubmissionEntity submission : submissions) {
            var submittingTeam = submission.getTeamMember().getTeam();
            var submittingTeamName = submittingTeam.getName();
            var submittingTeamColor = submittingTeam.getColor();

            int pointsEarned = calculatePointsEarned(submission, submittingTeamName);
            int pointsLost = calculatePointsLost(submission, submittingTeamName);

            updateTeamPoints(submittingTeamName, pointsEarned, teamPointsMap);
            updateTeamPoints(submission.getFlag().getFlagOwner().getName(), pointsLost, teamPointsMap);

            result.add(createScoreChangeDto(
                    submittingTeamName, submittingTeamColor, submission.getSubmissionTime(), pointsEarned, teamPointsMap)
            );

            result.add(createScoreChangeDto(submission.getFlag().getFlagOwner().getName(),
                    submission.getFlag().getFlagOwner().getColor(),
                    submission.getSubmissionTime(), -pointsLost, teamPointsMap)
            );
        }

        var startLocalTime = competitionService.getCompetitionDto().startDate();
        if (startLocalTime != null) {
            var startTime = Timestamp.valueOf(startLocalTime);
            initializeTeamsWithZeroPoints(teamPointsMap, result, startTime);
        }

        return result;
    }

    private void initializeTeamsWithZeroPoints(Map<String, Integer> teamPointsMap,
                                               List<TeamScoreChangeDto> result, Date startTime) {
        var allTeams = teamRepository.findAll();
        for (TeamEntity team : allTeams) {
            String teamName = team.getName();
            if (!teamPointsMap.containsKey(teamName)) {
                teamPointsMap.put(teamName, 0);
            }
            result.add(new TeamScoreChangeDto(teamName, startTime, 0, 0, team.getColor()));
        }
    }

    private int calculatePointsEarned(FlagSubmissionEntity submission, String submittingTeam) {
        int pointsEarned = 0;
        if (submission.getIsCorrect() && submission.getFlag() != null) {
            String flagOwnerTeam = submission.getFlag().getFlagOwner().getName();
            if (!submittingTeam.equals(flagOwnerTeam)) {
                pointsEarned = flagCostProperties.getFlagCost();
            }
        }
        return pointsEarned;
    }

    private int calculatePointsLost(FlagSubmissionEntity submission, String submittingTeam) {
        int pointsLost = 0;
        if (submission.getIsCorrect() && submission.getFlag() != null) {
            String flagOwnerTeam = submission.getFlag().getFlagOwner().getName();
            if (!submittingTeam.equals(flagOwnerTeam)) {
                pointsLost = -flagCostProperties.getFlagLost();
            }
        }
        return pointsLost;
    }

    private void updateTeamPoints(String teamName, int points, Map<String, Integer> teamPointsMap) {
        teamPointsMap.put(teamName, teamPointsMap.getOrDefault(teamName, 0) + points);
    }

    private TeamScoreChangeDto createScoreChangeDto(
            String teamName,
            String teamColor,
            Date time,
            int points,
            Map<String, Integer> teamPointsMap
    ) {
        return new TeamScoreChangeDto(
                teamName,
                time,
                points,
                teamPointsMap.get(teamName),
                teamColor
        );
    }
}

