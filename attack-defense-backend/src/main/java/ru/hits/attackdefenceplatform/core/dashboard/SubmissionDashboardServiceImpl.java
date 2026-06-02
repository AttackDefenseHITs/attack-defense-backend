package ru.hits.attackdefenceplatform.core.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.dashboard.repository.spec.FlagSubmissionSpecifications;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Реализация сервиса для работы с дашбордом, включая фильтрацию сабмита флагов и расчет изменений счета команд.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionDashboardServiceImpl implements DashboardService {

    private final FlagSubmissionRepository flagSubmissionRepository;
    private final TeamRepository teamRepository;
    private final CompetitionContext competitionContext;

    @Override
    public List<TeamScoreChangeDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId) {
        Specification<FlagSubmissionEntity> spec = FlagSubmissionSpecifications.createSpecification(isCorrect, teamId);
        var submissions = flagSubmissionRepository.findAll(spec);
        return convertSubmissionsToDTO(submissions);
    }

    private List<TeamScoreChangeDto> convertSubmissionsToDTO(List<FlagSubmissionEntity> submissions) {
        Map<String, Double> teamPointsMap = new HashMap<>();
        List<TeamScoreChangeDto> result = new ArrayList<>();
        CompetitionDto competition = competitionContext.getCompetitionDto();

        for (FlagSubmissionEntity submission : submissions) {
            if (submission.getFlag() == null || submission.getFlag().getFlagOwner() == null) {
                continue;
            }

            if (Boolean.TRUE.equals(submission.getTeam().getIsSystem())) {
                continue;
            }

            if (Boolean.TRUE.equals(submission.getFlag().getFlagOwner().getIsSystem())) {
                continue;
            }

            var submittingTeam = submission.getTeam();
            var submittingTeamName = submittingTeam.getName();
            var submittingTeamColor = submittingTeam.getColor();

            double pointsEarned = calculatePointsEarned(submission, submittingTeamName, competition);
            double pointsLost = calculatePointsLost(submission, submittingTeamName, competition);

            updateTeamPoints(submittingTeamName, pointsEarned, teamPointsMap);
            updateTeamPoints(submission.getFlag().getFlagOwner().getName(), pointsLost, teamPointsMap);

            result.add(createScoreChangeDto(
                    submittingTeamName,
                    submittingTeamColor,
                    submission.getSubmissionTime(),
                    pointsEarned,
                    teamPointsMap)
            );

            result.add(createScoreChangeDto(
                    submission.getFlag().getFlagOwner().getName(),
                    submission.getFlag().getFlagOwner().getColor(),
                    submission.getSubmissionTime(),
                    -pointsLost,
                    teamPointsMap)
            );
        }

        var startLocalTime = competitionContext.getCurrent().getStartDate();
        if (startLocalTime != null) {
            var startTime = Timestamp.valueOf(startLocalTime);
            initializeTeamsWithZeroPoints(teamPointsMap, result, startTime);
        }

        return result;
    }

    private void initializeTeamsWithZeroPoints(Map<String, Double> teamPointsMap,
                                               List<TeamScoreChangeDto> result,
                                               Date startTime) {
        var allTeams = teamRepository.findAllByIsSystemFalse();
        for (TeamEntity team : allTeams) {
            String teamName = team.getName();
            if (!teamPointsMap.containsKey(teamName)) {
                teamPointsMap.put(teamName, 0.0);
            }
            result.add(new TeamScoreChangeDto(teamName, startTime, 0.0, 0.0, team.getColor()));
        }
    }

    private double calculatePointsEarned(FlagSubmissionEntity submission, String submittingTeam, CompetitionDto competitionDto) {
        double pointsEarned = 0.0;
        if (submission.getIsCorrect() && submission.getFlag() != null) {
            String flagOwnerTeam = submission.getFlag().getFlagOwner().getName();
            if (!submittingTeam.equals(flagOwnerTeam)) {
                pointsEarned = submission.getPointsAwarded();
            }
        }
        return pointsEarned;
    }

    private double calculatePointsLost(FlagSubmissionEntity submission, String submittingTeam, CompetitionDto competitionDto) {
        double pointsLost = 0.0;
        if (submission.getIsCorrect() && submission.getFlag() != null) {
            String flagOwnerTeam = submission.getFlag().getFlagOwner().getName();
            if (!submittingTeam.equals(flagOwnerTeam)) {
                pointsLost = -competitionDto.flagLostCost();
            }
        }
        return pointsLost;
    }

    private void updateTeamPoints(String teamName, Double points, Map<String, Double> teamPointsMap) {
        teamPointsMap.put(teamName, teamPointsMap.getOrDefault(teamName, 0.0) + points);
    }

    private TeamScoreChangeDto createScoreChangeDto(
            String teamName,
            String teamColor,
            Date time,
            double points,
            Map<String, Double> teamPointsMap
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


