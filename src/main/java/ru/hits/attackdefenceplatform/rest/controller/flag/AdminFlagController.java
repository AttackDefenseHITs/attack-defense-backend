package ru.hits.attackdefenceplatform.rest.controller.flag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.flag.AdminFlagService;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagListDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/flags")
@Tag(name = "Управление флагами команд для администратора")
@RequiredArgsConstructor
public class AdminFlagController {

    private final AdminFlagService adminFlagService;

    @GetMapping
    @Operation(summary = "Получить все флаги")
    public ResponseEntity<List<FlagListDto>> getAllFlags() {
        var flags = adminFlagService.getAllFlags();
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о флаге")
    public ResponseEntity<FlagDto> getFlagById(@PathVariable UUID id) {
        var flagDto = adminFlagService.getFlagById(id);
        return ResponseEntity.ok(flagDto);
    }

    @GetMapping("/service/{serviceId}")
    @Operation(summary = "Получить флаги всех команд для конкретного сервиса")
    public ResponseEntity<List<FlagListDto>> getFlagsByService(@PathVariable UUID serviceId) {
        var flags = adminFlagService.getFlagsByService(serviceId);
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Получить все флаги конкретной команды")
    public ResponseEntity<List<FlagListDto>> getFlagsByTeam(@PathVariable UUID teamId) {
        var flags = adminFlagService.getFlagsByTeam(teamId);
        return ResponseEntity.ok(flags);
    }
}
