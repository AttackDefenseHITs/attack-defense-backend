package ru.hits.attackdefenceplatform.core.attack_bot.rules;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotContext;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamAttackResult;

@Component
public class SlaPriorityRule extends BasePriorityRule {

    @Override
    protected TeamAttackResult handle(AttackBotContext ctx, TeamAttackResult current) {
        if (!current.isEligible()) {
            return current;
        }

        double sla = ctx.getCurrentSla()
                .getOrDefault(current.getTeamId(), 0.0);

        double weight = ctx.getSettings().getPrioritySlaWeight();
        current.setPriority(current.getPriority() + sla * weight);
        return current;
    }
}

