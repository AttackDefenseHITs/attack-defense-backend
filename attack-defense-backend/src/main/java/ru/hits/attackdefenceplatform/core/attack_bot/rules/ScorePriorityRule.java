package ru.hits.attackdefenceplatform.core.attack_bot.rules;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotContext;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamAttackResult;

@Component
public class ScorePriorityRule extends BasePriorityRule {

    @Override
    protected TeamAttackResult handle(AttackBotContext ctx, TeamAttackResult current) {
        if (!current.isEligible()) {
            return current;
        }

        double score = ctx.getCurrentScores()
                .getOrDefault(current.getTeamId(), 0.0);

        if (score <= 0.0) {
            current.setEligible(false);
            current.setPriority(0.0);
            return current;
        }

        double weight = ctx.getSettings().getPriorityScoreWeight();
        current.setPriority(current.getPriority() + score * weight);
        return current;
    }
}

