package ru.hits.attackdefenceplatform.core.attack_bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.attack_bot.metric.AttackBotStateStore;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamInfo;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.points.PointsService;
import ru.hits.attackdefenceplatform.core.points.sla.SlaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttackBotContextBuilder {

    private final AttackBotConfigurationService settingsService;
    private final TeamRepository teamRepository;
    private final PointsService pointsService;
    private final SlaService slaService;
    private final AttackBotStateStore stateStore;

    public AttackBotContext build(long roundNumber) {
        var settings = settingsService.getSettings();
        var teams = teamRepository.findAllByIsSystemFalse();

        List<TeamInfo> teamInfos = teams.stream()
                .map(t -> new TeamInfo(t.getId(), t.getName()))
                .toList();

        Map<UUID, Double> scores = new HashMap<>();
        Map<UUID, Double> sla = new HashMap<>();
        Map<UUID, Long> lastAttack = new HashMap<>();

        for (TeamEntity t : teams) {
            double score = pointsService.calculateTeamFlagPoints(t);
            scores.put(t.getId(), score);

            double slaValue = slaService.getTeamSla(t.getId());
            sla.put(t.getId(), slaValue);

            long lastRound = stateStore.getLastAttackRound(t.getId());
            lastAttack.put(t.getId(), lastRound);
        }

        return new AttackBotContext(
                roundNumber,
                settings,
                teamInfos,
                scores,
                sla,
                lastAttack
        );
    }
}
