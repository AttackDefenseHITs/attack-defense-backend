package ru.hits.attackdefenceplatform.core.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.FlagCostProperties;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.dashboard.repository.spec.FlagSubmissionSpecifications;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService{

    private final FlagSubmissionRepository flagSubmissionRepository;
    private final FlagCostProperties flagCostProperties;

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
            String submittingTeam = submission.getTeamMember().getTeam().getName();
            int pointsEarned = 0;
            int pointsLost;

            if (submission.getIsCorrect() && submission.getFlag() != null) {
                String flagOwnerTeam = submission.getFlag().getFlagOwner().getName();

                if (!submittingTeam.equals(flagOwnerTeam)) {
                    pointsEarned = flagCostProperties.getFlagCost();
                    pointsLost = flagCostProperties.getFlagLost();
                    teamPointsMap.put(submittingTeam, teamPointsMap.getOrDefault(submittingTeam, 0) + pointsEarned);

                    teamPointsMap.put(flagOwnerTeam, teamPointsMap.getOrDefault(flagOwnerTeam, 0) - pointsLost);

                    result.add(new TeamScoreChangeDto(
                            flagOwnerTeam,
                            submission.getSubmissionTime(),
                            -pointsLost,
                            teamPointsMap.get(flagOwnerTeam)
                    ));
                } else {
                    pointsEarned = 0;
                }
            }

            teamPointsMap.put(submittingTeam, teamPointsMap.getOrDefault(submittingTeam, 0));

            result.add(new TeamScoreChangeDto(
                    submittingTeam,
                    submission.getSubmissionTime(),
                    pointsEarned,
                    teamPointsMap.get(submittingTeam)
            ));
        }

        return result;
    }
}
