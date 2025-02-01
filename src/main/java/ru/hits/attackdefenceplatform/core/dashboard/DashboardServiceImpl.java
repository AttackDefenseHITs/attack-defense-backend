package ru.hits.attackdefenceplatform.core.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.FlagCostProperties;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.dashboard.repository.spec.FlagSubmissionSpecifications;
import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionWithPointsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService{

    private final FlagSubmissionRepository flagSubmissionRepository;
    private final FlagCostProperties flagCostProperties;

    @Override
    public List<FlagSubmissionWithPointsDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId) {
        Specification<FlagSubmissionEntity> spec = FlagSubmissionSpecifications.createSpecification(isCorrect, teamId);

        var submissions = flagSubmissionRepository.findAll(spec);

        return convertSubmissionsToDTO(submissions);
    }

    private List<FlagSubmissionWithPointsDto> convertSubmissionsToDTO(List<FlagSubmissionEntity> submissions) {
        int totalTeamPoints = 0;
        List<FlagSubmissionWithPointsDto> result = new ArrayList<>();

        for (FlagSubmissionEntity submission : submissions) {
            int flagPoints = submission.getFlag() != null ? flagCostProperties.getFlagCost() : 0;
            if (submission.getIsCorrect()) {
                totalTeamPoints += flagPoints;
            }
            result.add(new FlagSubmissionWithPointsDto(
                    submission.getId(),
                    submission.getTeamMember().getTeam().getName(),
                    submission.getTeamMember().getUser().getName(),
                    submission.getSubmittedFlag(),
                    submission.getSubmissionTime(),
                    submission.getIsCorrect(),
                    flagPoints,
                    totalTeamPoints
            ));
        }

        return result;
    }
}
