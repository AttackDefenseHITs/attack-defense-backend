package ru.hits.attackdefenceplatform.rest.controller.attack_bot;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotConfigurationService;
import ru.hits.attackdefenceplatform.public_interface.attack_bot.AttackBotSettingsDto;

@RestController
@RequestMapping("/api/admin/attack-bot")
@RequiredArgsConstructor
@Tag(name = "Управление настройками атакующего бота")
public class AttackBotSettingsController {

    private final AttackBotConfigurationService service;

    @GetMapping
    public AttackBotSettingsDto get() {
        return service.getSettings();
    }

    @PostMapping
    public AttackBotSettingsDto update(@RequestBody AttackBotSettingsDto dto) {
        return service.updateSettings(dto);
    }
}
