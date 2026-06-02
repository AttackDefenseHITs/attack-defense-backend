package ru.hits.attackdefenceplatform.core.attack_bot.rules;

import lombok.Setter;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotContext;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamAttackResult;

public abstract class BasePriorityRule {

    @Setter
    protected BasePriorityRule next;

    public TeamAttackResult apply(AttackBotContext ctx, TeamAttackResult current) {
        TeamAttackResult updated = handle(ctx, current);
        if (next != null) {
            return next.apply(ctx, updated);
        }
        return updated;
    }

    protected abstract TeamAttackResult handle(AttackBotContext ctx, TeamAttackResult current);
}

