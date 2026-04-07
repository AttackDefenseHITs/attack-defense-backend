package ru.hits.attackdefenceplatform.core.dashboard.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.dashboard.DashboardService;
import ru.hits.attackdefenceplatform.core.dashboard.RoundSnapshotDashboardServiceImpl;
import ru.hits.attackdefenceplatform.core.dashboard.SubmissionDashboardServiceImpl;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class DashboardServiceFacade implements DashboardService {
    private final SubmissionDashboardServiceImpl submissionDashboardService;
    private final RoundSnapshotDashboardServiceImpl roundSnapshotDashboardService;
    private final CompetitionContext competitionContext;

    @Override
    public List<TeamScoreChangeDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId) {
        var mode = competitionContext.getMode();
        return switch (mode) {
            case ATTACK_DEFENSE -> submissionDashboardService.getFilteredSubmissions(isCorrect, teamId);
            case REVERSE_DEFENSE -> roundSnapshotDashboardService.getFilteredSubmissions(isCorrect, teamId);
        };
    }
}
