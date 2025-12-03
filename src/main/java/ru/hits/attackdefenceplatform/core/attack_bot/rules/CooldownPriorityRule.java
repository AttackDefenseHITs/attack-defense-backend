package ru.hits.attackdefenceplatform.core.attack_bot.rules;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotContext;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamAttackResult;

@Component
public class CooldownPriorityRule extends BasePriorityRule {

    @Override
    protected TeamAttackResult handle(AttackBotContext ctx, TeamAttackResult current) {
        if (!current.isEligible()) {
            return current;
        }

        int cooldownRounds = ctx.getSettings().getCooldownRounds();
        if (cooldownRounds <= 0) {
            return current;
        }

        long lastRound = ctx.getLastAttackRoundForTeam()
                .getOrDefault(current.getTeamId(), -1L);

        long currentRound = ctx.getRoundTimestamp();
        boolean inCooldown = lastRound >= 0 && (currentRound - lastRound) < cooldownRounds;

        if (inCooldown) {
            current.setEligible(false);
            current.setPriority(0.0);
        }

        return current;
    }
}

