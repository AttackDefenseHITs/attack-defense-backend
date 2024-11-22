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
import ru.hits.attackdefenceplatform.core.flag.FlagService;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagListDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flags")
@Tag(name = "Управление флагами команд для администратора")
@RequiredArgsConstructor
public class FlagController {

    private final FlagService flagService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать флаг")
    public ResponseEntity<FlagDto> createFlag(@RequestBody CreateFlagRequest request) {
        var flagDto = flagService.createFlag(request);
        return ResponseEntity.ok(flagDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все флаги")
    public ResponseEntity<List<FlagListDto>> getAllFlags() {
        var flags = flagService.getAllFlags();
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить информацию о флаге")
    public ResponseEntity<FlagDto> getFlagById(@PathVariable UUID id) {
        var flagDto = flagService.getFlagById(id);
        return ResponseEntity.ok(flagDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить флаг")
    public ResponseEntity<Void> deleteFlag(@PathVariable UUID id) {
        flagService.deleteFlag(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить данные о флаге")
    public ResponseEntity<Void> updateFlag(@PathVariable UUID id, @RequestBody CreateFlagRequest request) {
        flagService.updateFlag(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить флаги всех команд для конкретного сервиса")
    public ResponseEntity<List<FlagListDto>> getFlagsByService(@PathVariable UUID serviceId) {
        var flags = flagService.getFlagsByService(serviceId);
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все флаги конкретной команды")
    public ResponseEntity<List<FlagListDto>> getFlagsByTeam(@PathVariable UUID teamId) {
        var flags = flagService.getFlagsByTeam(teamId);
        return ResponseEntity.ok(flags);
    }
}
