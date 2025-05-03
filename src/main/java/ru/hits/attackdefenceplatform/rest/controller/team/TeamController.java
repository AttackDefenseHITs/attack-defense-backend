package ru.hits.attackdefenceplatform.rest.controller.team;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.team.TeamService;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.team.CreateManyTeamsRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;
import ru.hits.attackdefenceplatform.public_interface.user.UserTeamMemberDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/team")
@Tag(name = "Команда")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/{teamId}/join")
    @Operation(summary = "Присоединиться к команде")
    public ResponseEntity<Void> joinToTeam(@PathVariable UUID teamId, @AuthenticationPrincipal UserEntity user) {
        teamService.joinToTeam(user, teamId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}/leave")
    @Operation(summary = "Выйти из команды")
    public ResponseEntity<Void> leftFromTeam(@PathVariable UUID teamId, @AuthenticationPrincipal UserEntity user) {
        teamService.leftFromTeam(user, teamId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о команде")
    public ResponseEntity<TeamInfoDto> getTeamById(@PathVariable UUID id, @AuthenticationPrincipal UserEntity user) {
        var teamInfo = teamService.getTeamById(id, user);
        return ResponseEntity.ok(teamInfo);
    }

    @GetMapping
    @Operation(summary = "Получить список всех команд")
    public ResponseEntity<List<TeamListDto>> getAllTeams(@AuthenticationPrincipal UserEntity user) {
        var teams = teamService.getAllTeams(user);
        return ResponseEntity.ok(teams);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Создать команду")
    public ResponseEntity<TeamListDto> createTeam(@RequestBody CreateTeamRequest request) {
        var team = teamService.createTeam(request);
        return ResponseEntity.ok(team);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить команду")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bulk")
    @Operation(summary = "Создать несколько команд")
    public ResponseEntity<List<TeamListDto>> createManyTeams(@RequestBody CreateManyTeamsRequest request) {
        var teams = teamService.createManyTeams(request);
        return ResponseEntity.ok(teams);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{teamId}")
    @Operation(summary = "Обновить данные команды")
    public ResponseEntity<Void> updateTeam(@PathVariable UUID teamId, @RequestBody CreateTeamRequest request) {
        teamService.updateTeam(teamId, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{teamId}/members/{userId}")
    @Operation(summary = "Удалить участника из команды")
    public ResponseEntity<Void> removeMemberFromTeam(
            @PathVariable UUID teamId,
            @PathVariable UUID userId
    ) {
        teamService.removeMemberFromTeam(teamId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/members-rating")
    @Operation(summary = "Получить рейтинг участников всех команд")
    public ResponseEntity<List<UserTeamMemberDto>> getMemberRating() {
        var membersList = teamService.getTeamMemberRatings();
        return ResponseEntity.ok(membersList);
    }
}

