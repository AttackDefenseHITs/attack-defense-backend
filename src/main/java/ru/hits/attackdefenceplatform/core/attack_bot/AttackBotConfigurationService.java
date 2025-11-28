package ru.hits.attackdefenceplatform.core.attack_bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @PostConstruct
    public void init() {
        this.cached = repository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .orElseThrow(() -> new IllegalStateException("Attack bot settings missing in DB"));
    }

    public AttackBotSettingsDto getSettings() {
        return map(cached);
    }

    @Transactional
    public AttackBotSettingsDto updateSettings(AttackBotSettingsDto dto) {
        AttackBotSettingsEntity entity = repository.findById(cached.getId())
                .orElseThrow();

        entity.setEnabled(dto.isEnabled());
        entity.setAttackProbability(dto.getAttackProbability());
        entity.setMaxTargets(dto.getMaxTargets());
        entity.setCooldownRounds(dto.getCooldownRounds());
        entity.setPriorityScoreWeight(dto.getPriorityScoreWeight());
        entity.setPrioritySlaWeight(dto.getPrioritySlaWeight());
        entity.setRoundIntervalSeconds(dto.getRoundIntervalSeconds());

        repository.save(entity);
        this.cached = entity;
        return map(entity);
    }

    private AttackBotSettingsDto map(AttackBotSettingsEntity e) {
        var dto = new AttackBotSettingsDto();
        dto.setEnabled(e.isEnabled());
        dto.setAttackProbability(e.getAttackProbability());
        dto.setMaxTargets(e.getMaxTargets());
        dto.setCooldownRounds(e.getCooldownRounds());
        dto.setPriorityScoreWeight(e.getPriorityScoreWeight());
        dto.setPrioritySlaWeight(e.getPrioritySlaWeight());
        dto.setRoundIntervalSeconds(e.getRoundIntervalSeconds());
        return dto;
    }
}