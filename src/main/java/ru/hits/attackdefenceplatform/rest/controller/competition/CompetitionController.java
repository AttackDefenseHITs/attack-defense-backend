package ru.hits.attackdefenceplatform.rest.controller.competition;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionStatus;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

@RestController
@RequestMapping("api/competition")
@Tag(name = "Управление соревнованием")
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;

    @PostMapping("/start")
    public ResponseEntity<String> startCompetition() {
        competitionService.startCompetition();
        return ResponseEntity.ok("Соревнование запущено");
    }

    @PostMapping("/complete")
    public ResponseEntity<String> completeCompetition() {
        competitionService.completeCompetition();
        return ResponseEntity.ok("Соревнование завершено");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelCompetition() {
        competitionService.cancelCompetition();
        return ResponseEntity.ok("Соревнование отменено");
    }

    @PostMapping("/pause")
    public ResponseEntity<String> pauseCompetition() {
        competitionService.pauseCompetition();
        return ResponseEntity.ok("Соревнование поставлено на паузу");
    }

    @PostMapping("/resume")
    public ResponseEntity<String> resumeCompetition() {
        competitionService.resumeCompetition();
        return ResponseEntity.ok("Соревнование возобновлено");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetCompetition() {
        competitionService.resetCompetition();
        return ResponseEntity.ok("Соревнование сброшено на NEW");
    }

    @PutMapping("/update")
    public ResponseEntity<CompetitionDto> updateCompetition(@RequestBody UpdateCompetitionRequest request) {
        var competition = competitionService.updateCompetition(request);
        return ResponseEntity.ok(competition);
    }

    @GetMapping
    public ResponseEntity<CompetitionDto> getCompetition() {
        var competitionDto = competitionService.getCompetitionDto();
        return ResponseEntity.ok(competitionDto);
    }
}
