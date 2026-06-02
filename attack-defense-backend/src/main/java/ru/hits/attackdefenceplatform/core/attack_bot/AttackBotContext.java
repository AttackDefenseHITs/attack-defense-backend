package ru.hits.attackdefenceplatform.core.attack_bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamInfo;
import ru.hits.attackdefenceplatform.public_interface.attack_bot.AttackBotSettingsDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AttackBotContext {

    private long roundTimestamp;

    private AttackBotSettingsDto settings;

    private List<TeamInfo> allTeams;

    private Map<UUID, Double> currentScores;

    private Map<UUID, Double> currentSla;

    private Map<UUID, Long> lastAttackRoundForTeam;
}
