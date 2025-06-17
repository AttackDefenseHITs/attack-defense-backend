package ru.hits.attackdefenceplatform.core.dashboard;

import org.springframework.data.domain.Page;
import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionDto;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    List<TeamScoreChangeDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId);
    Page<FlagSubmissionDto> getFlagSubmissions(int page, int size);
}
