package ru.hits.attackdefenceplatform.core.attack_bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamAttackResult;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TargetTeamSelector {

    private final AttackPriorityCalculator priorityCalculator;
    private final Random random = new Random();

    public List<TeamInfo> selectTargets(AttackBotContext ctx) {
        // считаем приоритет для каждой команды
        List<TeamAttackResult> results = ctx.getAllTeams().stream()
                .map(team -> priorityCalculator.calculate(team.getId(), ctx))
                .filter(r -> r.isEligible() && r.getPriority() > 0.0)
                .toList();

        if (results.isEmpty()) {
            return List.of();
        }

        int maxTargets = Math.min(ctx.getSettings().getMaxTargets(), results.size());
        if (maxTargets <= 0) {
            return List.of();
        }

        List<TeamInfo> selected = new ArrayList<>();
        Set<UUID> used = new HashSet<>();

        for (int i = 0; i < maxTargets; i++) {
            TeamAttackResult chosen = chooseOne(results, used);
            if (chosen == null) break;

            used.add(chosen.getTeamId());
            ctx.getAllTeams().stream()
                    .filter(t -> t.getId().equals(chosen.getTeamId()))
                    .findFirst()
                    .ifPresent(selected::add);
        }

        return selected;
    }

    private TeamAttackResult chooseOne(List<TeamAttackResult> all, Set<UUID> used) {
        List<TeamAttackResult> candidates = all.stream()
                .filter(r -> !used.contains(r.getTeamId()))
                .toList();

        if (candidates.isEmpty()) {
            return null;
        }

        double total = candidates.stream()
                .mapToDouble(TeamAttackResult::getPriority)
                .sum();

        if (total <= 0.0) {
            return null;
        }

        double r = random.nextDouble() * total;
        double acc = 0.0;

        for (TeamAttackResult c : candidates) {
            acc += c.getPriority();
            if (r <= acc) {
                return c;
            }
        }

        return candidates.get(candidates.size() - 1);
    }
}

