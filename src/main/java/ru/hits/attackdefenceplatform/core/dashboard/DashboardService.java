package ru.hits.attackdefenceplatform.core.dashboard;

import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionWithPointsDto;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    List<FlagSubmissionWithPointsDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId);
}
