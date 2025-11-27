package ru.hits.attackdefenceplatform.core.attack_bot.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "attack_bot_settings")
@Data
public class AttackBotSettingsEntity {

    @Id
    private UUID id;

    private boolean enabled;
    private double attackProbability;
    private int maxTargets;
    private int cooldownRounds;
    private double priorityScoreWeight;
    private double prioritySlaWeight;
    private int roundIntervalSeconds;
}
