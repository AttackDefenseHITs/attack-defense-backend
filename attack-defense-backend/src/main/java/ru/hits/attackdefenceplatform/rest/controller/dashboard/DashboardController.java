package ru.hits.attackdefenceplatform.rest.controller.dashboard;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.dashboard.DashboardService;
import ru.hits.attackdefenceplatform.core.dashboard.FlagSubmissionService;
import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionDto;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "Дашборд")
public class DashboardController {
    private final DashboardService dashboardService;
    private final FlagSubmissionService flagSubmissionService;

    @GetMapping
    public List<TeamScoreChangeDto> getFilteredSubmissions(
            @RequestParam(required = false) Boolean isCorrect,
            @RequestParam(required = false) UUID teamId
    ) {
        return dashboardService.getFilteredSubmissions(isCorrect, teamId);
    }

    @GetMapping("/submissions")
    public ResponseEntity<Page<FlagSubmissionDto>> getFilteredSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isCorrect
    ) {
        return ResponseEntity.ok(flagSubmissionService.getFlagSubmissions(page, size, search, isCorrect));
    }
}
