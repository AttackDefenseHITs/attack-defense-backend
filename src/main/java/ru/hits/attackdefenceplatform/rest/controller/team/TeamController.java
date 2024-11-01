package ru.hits.attackdefenceplatform.rest.controller.team;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.team.TeamService;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;

import java.util.UUID;

@RestController
@RequestMapping("api/team")
@Tag(name = "Команда")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Создать команду")
    public ResponseEntity<UUID> createTeam(@RequestBody CreateTeamRequest request) {
        UUID teamId = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(teamId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить команду")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/join")
    @Operation(summary = "Присоединиться к команде")
    public ResponseEntity<Void> joinToTeam(@PathVariable UUID teamId, @AuthenticationPrincipal UserEntity user) {
        teamService.joinToTeam(user, teamId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{teamId}/leave")
    @Operation(summary = "Выйти из команды")
    public ResponseEntity<Void> leftFromTeam(@PathVariable UUID teamId, @AuthenticationPrincipal UserEntity user) {
        teamService.leftFromTeam(user, teamId);
        return ResponseEntity.noContent().build();
    }
}

