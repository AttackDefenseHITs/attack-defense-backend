package ru.hits.attackdefenceplatform.core.attack_bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.attack_bot.mapper.AttackBotSettingsMapper;
import ru.hits.attackdefenceplatform.core.attack_bot.repository.AttackBotSettingsEntity;
import ru.hits.attackdefenceplatform.core.attack_bot.repository.AttackBotSettingsRepository;
import ru.hits.attackdefenceplatform.public_interface.attack_bot.AttackBotSettingsDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttackBotConfigurationService {

    private final AttackBotSettingsRepository repository;
    private volatile AttackBotSettingsEntity cached;
    private static final String settingsUUID = "00000000-0000-0000-0000-000000000001";

    @PostConstruct
    public void init() {
        this.cached = repository.findById(UUID.fromString(settingsUUID))
                .orElseThrow(() -> new IllegalStateException("Attack bot settings missing in DB"));
    }

    public AttackBotSettingsDto getSettings() {
        return AttackBotSettingsMapper.toDto(cached);
    }

    @Transactional
    public AttackBotSettingsDto updateSettings(AttackBotSettingsDto dto) {
        AttackBotSettingsEntity entity = repository.findById(cached.getId())
                .orElseThrow();

        AttackBotSettingsMapper.updateEntity(entity, dto);
        repository.save(entity);

        cached = entity;
        return AttackBotSettingsMapper.toDto(entity);
    }
}
