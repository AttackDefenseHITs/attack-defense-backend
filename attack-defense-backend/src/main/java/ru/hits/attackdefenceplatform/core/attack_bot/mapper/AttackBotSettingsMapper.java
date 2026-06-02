package ru.hits.attackdefenceplatform.core.attack_bot.mapper;

import lombok.experimental.UtilityClass;
import ru.hits.attackdefenceplatform.core.attack_bot.repository.AttackBotSettingsEntity;
import ru.hits.attackdefenceplatform.public_interface.attack_bot.AttackBotSettingsDto;

@UtilityClass
public class AttackBotSettingsMapper {

    public AttackBotSettingsDto toDto(AttackBotSettingsEntity e) {
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

    public void updateEntity(AttackBotSettingsEntity e, AttackBotSettingsDto dto) {
        e.setEnabled(dto.isEnabled());
        e.setAttackProbability(dto.getAttackProbability());
        e.setMaxTargets(dto.getMaxTargets());
        e.setCooldownRounds(dto.getCooldownRounds());
        e.setPriorityScoreWeight(dto.getPriorityScoreWeight());
        e.setPrioritySlaWeight(dto.getPrioritySlaWeight());
        e.setRoundIntervalSeconds(dto.getRoundIntervalSeconds());
    }
}

