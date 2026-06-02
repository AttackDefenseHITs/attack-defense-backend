package ru.hits.attackdefenceplatform.public_interface.attack_bot;

import lombok.Data;

@Data
public class AttackBotSettingsDto {
    private boolean enabled;
    private double attackProbability;
    private int maxTargets;
    private int cooldownRounds;
    private double priorityScoreWeight;
    private double prioritySlaWeight;
    private int roundIntervalSeconds;
}

