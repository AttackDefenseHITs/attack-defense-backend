package ru.hits.attackdefenceplatform.core.attack_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttackBotSettingsRepository extends JpaRepository<AttackBotSettingsEntity, UUID> {
}
