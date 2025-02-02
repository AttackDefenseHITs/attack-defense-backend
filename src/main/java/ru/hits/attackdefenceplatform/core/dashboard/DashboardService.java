package ru.hits.attackdefenceplatform.core.dashboard;

import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    List<TeamScoreChangeDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId);
}
