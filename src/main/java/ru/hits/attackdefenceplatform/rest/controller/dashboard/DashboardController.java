package ru.hits.attackdefenceplatform.rest.controller.dashboard;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.dashboard.DashboardService;
import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionWithPointsDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "Дашборд")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public List<FlagSubmissionWithPointsDto> getFilteredSubmissions(
            @RequestParam(required = false) Boolean isCorrect,
            @RequestParam(required = false) UUID teamId
    ) {
        return dashboardService.getFilteredSubmissions(isCorrect, teamId);
    }
}
