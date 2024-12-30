package ru.hits.attackdefenceplatform.rest.controller.competition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionStatus;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

import java.util.List;

@RestController
@RequestMapping("api/competition")
@Tag(name = "Управление соревнованием")
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;

    @PostMapping("/status")
    @Operation(summary = "Изменить статус соревнования")
    public ResponseEntity<CompetitionDto> changeCompetitionStatus(@RequestParam CompetitionAction action) {
        var competition = competitionService.changeCompetitionStatus(action);
        return ResponseEntity.ok(competition);
    }

    @GetMapping("/available")
    @Operation(summary = "Получить доступные действия с соревнованием")
    public ResponseEntity<List<CompetitionAction>> getAvailableCompetitionAction(){
        return ResponseEntity.ok(competitionService.getAvailableActions());
    }

    @PutMapping("/update")
    @Operation(summary = "Изменить настройки соревнования")
    public ResponseEntity<CompetitionDto> updateCompetition(@RequestBody UpdateCompetitionRequest request) {
        var competition = competitionService.updateCompetition(request);
        return ResponseEntity.ok(competition);
    }

    @GetMapping
    @Operation(summary = "Получить настройки соревнования")
    public ResponseEntity<CompetitionDto> getCompetition() {
        var competitionDto = competitionService.getCompetitionDto();
        return ResponseEntity.ok(competitionDto);
    }
}
