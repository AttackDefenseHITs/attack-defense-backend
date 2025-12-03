package ru.hits.attackdefenceplatform.core.attack_bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.attack_bot.model.TeamAttackResult;
import ru.hits.attackdefenceplatform.core.attack_bot.rules.CooldownPriorityRule;
import ru.hits.attackdefenceplatform.core.attack_bot.rules.ScorePriorityRule;
import ru.hits.attackdefenceplatform.core.attack_bot.rules.SlaPriorityRule;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttackPriorityCalculator {

    private final ScorePriorityRule scorePriorityRule;
    private final SlaPriorityRule slaPriorityRule;
    private final CooldownPriorityRule cooldownPriorityRule;

    @PostConstruct
    public void initChain() {
        scorePriorityRule.setNext(slaPriorityRule);
        slaPriorityRule.setNext(cooldownPriorityRule);
    }

    public TeamAttackResult calculate(UUID teamId, AttackBotContext ctx) {
        TeamAttackResult initial = new TeamAttackResult(teamId, 0.0, true);
        return scorePriorityRule.apply(ctx, initial);
    }
}

