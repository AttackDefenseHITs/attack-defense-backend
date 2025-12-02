package ru.hits.attackdefenceplatform.core.competition.mode.attack_defense;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;
import ru.hits.attackdefenceplatform.core.competition.mode.CompetitionModeModule;
import ru.hits.attackdefenceplatform.core.competition.mode.FlagSubmissionPolicy;
import ru.hits.attackdefenceplatform.core.competition.mode.RoundPolicy;
import ru.hits.attackdefenceplatform.core.competition.mode.ScoringPolicy;
import ru.hits.attackdefenceplatform.modules.checker.CheckerExecutionService;
import ru.hits.attackdefenceplatform.modules.points.DynamicAttackDefensePointsCounter;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttackDefenseModeModule implements CompetitionModeModule {

    private final DynamicAttackDefensePointsCounter dynamicPointsCounter;
    private final AttackDefenseFlagSubmissionPolicy flagPolicy;
    private final CheckerExecutionService checkerExecutionService;

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
        };
    }
}