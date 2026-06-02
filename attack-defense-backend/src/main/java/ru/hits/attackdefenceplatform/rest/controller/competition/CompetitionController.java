package ru.hits.attackdefenceplatform.rest.controller.competition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotConfigurationService;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.CompetitionSettingsFacade;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.public_interface.competition.ChangeStatusRequest;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionModeDto;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionSettingsDto;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionShortDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionModeRequest;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

import java.util.List;

@RestController
@RequestMapping("api/competition")
@Tag(name = "Управление соревнованием")
@RequiredArgsConstructor
public class CompetitionController {
    private final CompetitionService competitionService;
    private final CompetitionSettingsFacade competitionSettingsFacade;
    private final AttackBotConfigurationService configurationService;

    @PostMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменить статус соревнования")
    public ResponseEntity<CompetitionDto> changeCompetitionStatus(@RequestBody ChangeStatusRequest request) {
        var competition = competitionService.changeCompetitionStatus(request.action());
        return ResponseEntity.ok(competition);
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить доступные действия с соревнованием")
    public ResponseEntity<List<CompetitionAction>> getAvailableCompetitionAction(){
        return ResponseEntity.ok(competitionService.getAvailableActions());
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменить настройки соревнования")
    public ResponseEntity<CompetitionDto> updateCompetition(@RequestBody UpdateCompetitionRequest request) {
        var competition = competitionService.updateCompetition(request);
        configurationService.updateSettings(request.attackBotSettings());
        return ResponseEntity.ok(competition);
    }

    @GetMapping
    @Operation(summary = "Получить данные о соревновании")
    public ResponseEntity<CompetitionShortDto> getCompetition() {
        var competitionDto = competitionService.getCompetitionShortDto();
        return ResponseEntity.ok(competitionDto);
    }

    @GetMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить полные настройки соревнования")
    public ResponseEntity<CompetitionSettingsDto> getCompetitionSettings() {
        var competitionDto = competitionSettingsFacade.getCurrentSettings();
        return ResponseEntity.ok(competitionDto);
    }

    @PostMapping("/restart")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Перезапустить соревнования")
    public ResponseEntity<CompetitionDto> restartCompetition(){
        var competitionDto = competitionService.restartCompetition();
        return ResponseEntity.ok(competitionDto);
    }

    @GetMapping("/mode")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить текущий режим соревнования")
    public ResponseEntity<CompetitionModeDto> getCompetitionMode(){
        var competitionMode = competitionService.getCompetition().getCompetitionMode().name();
        return ResponseEntity.ok(new CompetitionModeDto(competitionMode));
    }

    @PostMapping("/mode")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Переключить режим соревнования")
    public ResponseEntity<CompetitionDto> setCompetitionMode(@RequestBody UpdateCompetitionModeRequest dto){
        return ResponseEntity.ok(competitionService.updateCompetitionMode(dto));
    }
}
