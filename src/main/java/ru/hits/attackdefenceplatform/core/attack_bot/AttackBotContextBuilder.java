package ru.hits.attackdefenceplatform.core.attack_bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttackBotContextBuilder {
    private final AttackBotConfigurationService settings;

    public AttackBotContext build(long round) {

        var cfg = settings.getSettings();
        return new AttackBotContext(round, cfg, null, null,  null, null);
    }
}