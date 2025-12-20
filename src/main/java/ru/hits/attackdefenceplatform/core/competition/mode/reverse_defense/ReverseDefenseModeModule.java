package ru.hits.attackdefenceplatform.core.competition.mode.reverse_defense;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;
import ru.hits.attackdefenceplatform.core.competition.mode.CompetitionModeModule;
import ru.hits.attackdefenceplatform.core.competition.mode.FlagSubmissionPolicy;
import ru.hits.attackdefenceplatform.core.competition.mode.RoundPolicy;
import ru.hits.attackdefenceplatform.core.competition.mode.ScoringPolicy;
import ru.hits.attackdefenceplatform.core.checker.CheckerExecutionService;
import ru.hits.attackdefenceplatform.core.points.ReverseDefensePointsCounter;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReverseDefenseModeModule implements CompetitionModeModule {

    private final ReverseDefensePointsCounter reverseDefensePointsCounter;
    private final ReverseDefenseFlagSubmissionPolicy flagPolicy;
    private final CheckerExecutionService checkerExecutionService;

    @Override
    public CompetitionMode mode() {
        return CompetitionMode.REVERSE_DEFENSE;
    }

    @Override
    public ScoringPolicy scoringPolicy() {
        return (competition, teamId) -> reverseDefensePointsCounter.getTeamPoints(teamId);
    }

    @Override
    public FlagSubmissionPolicy flagSubmissionPolicy() {
        return flagPolicy;
    }

    @Override
    public RoundPolicy roundPolicy() {
        return (competition, round) -> {
            if (round <= 0) {
                log.info("Раунд №0 — чекеры не запускаем (режим Reverse-Defense)");
                return;
            }
            checkerExecutionService.runAllCheckers(List.of("check", "put", "get"));
        };
    }
}
