package ru.hits.attackdefenceplatform.rest.controller.hint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.hint.HintAdminService;
import ru.hits.attackdefenceplatform.public_interface.hint.CreateServiceHintRequest;
import ru.hits.attackdefenceplatform.public_interface.hint.GetAllAdminHintsResponse;
import ru.hits.attackdefenceplatform.public_interface.hint.ServiceHintTemplateDto;
import ru.hits.attackdefenceplatform.public_interface.hint.SetHintEnabledRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/hints")
@Tag(name = "Управление подсказками для администратора")
public class HintAdminController {

    private final HintAdminService adminService;

    @Operation(summary = "Получить все подсказки (включая disabled), сгруппированные по сервисам")
    @GetMapping
    public GetAllAdminHintsResponse getAll() {
        return adminService.getAllHints();
    }

    @Operation(summary = "Создать подсказку (level будет присвоен автоматически как \"номер подсказки\")")
    @PostMapping
    public ServiceHintTemplateDto create(@RequestBody CreateServiceHintRequest req) {
        double multiplier = req.multiplier() / 100.0;
        return adminService.createHint(req.serviceId(), req.text(), multiplier);
    }

    @Operation(summary = "Включить/выключить подсказку")
    @PutMapping("/{hintTemplateId}/enabled")
    public void setEnabled(
            @PathVariable UUID hintTemplateId,
            @RequestBody SetHintEnabledRequest req
    ) {
        adminService.setEnabled(hintTemplateId, req.enabled());
    }

    @Operation(summary = "Удалить подсказку + перенумерация уровней")
    @DeleteMapping("/{hintTemplateId}")
    public void delete(@PathVariable UUID hintTemplateId) {
        adminService.deleteHint(hintTemplateId);
    }
}