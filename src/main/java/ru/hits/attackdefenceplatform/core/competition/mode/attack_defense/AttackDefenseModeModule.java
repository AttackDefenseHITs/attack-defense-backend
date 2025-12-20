package ru.hits.attackdefenceplatform.core.competition.mode.attack_defense;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotRunner;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;
import ru.hits.attackdefenceplatform.core.competition.mode.CompetitionModeModule;
import ru.hits.attackdefenceplatform.core.competition.mode.FlagSubmissionPolicy;
import ru.hits.attackdefenceplatform.core.competition.mode.RoundPolicy;
import ru.hits.attackdefenceplatform.core.competition.mode.ScoringPolicy;
import ru.hits.attackdefenceplatform.core.checker.CheckerExecutionService;
import ru.hits.attackdefenceplatform.core.points.DynamicAttackDefensePointsCounter;

import java.util.List;

@Component
@Slf4j
public class AttackDefenseModeModule implements CompetitionModeModule {

    private final DynamicAttackDefensePointsCounter dynamicPointsCounter;
    private final AttackDefenseFlagSubmissionPolicy flagPolicy;
    private final CheckerExecutionService checkerExecutionService;
    private final AttackBotRunner attackBotRunner;

    public AttackDefenseModeModule(
            DynamicAttackDefensePointsCounter dynamicPointsCounter,
            CheckerExecutionService checkerExecutionService,
            @Lazy AttackBotRunner attackBotRunner,
            AttackDefenseFlagSubmissionPolicy flagPolicy
    ) {
        this.checkerExecutionService = checkerExecutionService;
        this.attackBotRunner = attackBotRunner;
        this.dynamicPointsCounter = dynamicPointsCounter;
        this.flagPolicy = flagPolicy;
    }

    @Override
    public CompetitionMode mode() {
        return CompetitionMode.ATTACK_DEFENSE;
    }

    @Override
    public ScoringPolicy scoringPolicy() {
        return (competition, teamId) -> dynamicPointsCounter.getTeamPoints(teamId);
    }

    @Override
    public FlagSubmissionPolicy flagSubmissionPolicy() {
        return flagPolicy;
    }

    @Override
    public RoundPolicy roundPolicy() {
        return (competition, round) -> {
            if (round <= 0) {
                log.info("Раунд №0 — чекеры не запускаем (режим Attack-Defense)");
                return;
            }
            checkerExecutionService.runAllCheckers(List.of("check", "put", "get", "get_flags"));
            attackBotRunner.runRoundAsync(round);
        };
    }
}