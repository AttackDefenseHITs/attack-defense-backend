package ru.hits.attackdefenceplatform.core.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;

@Component
@RequiredArgsConstructor
public class PointsCounterStrategyFactory {
    private final AttackDefensePointsCounter attackDefensePointsCounter;
    private final ReverseDefensePointsCounter reverseDefensePointsCounter;

    public PointsCounterStrategy getStrategy(CompetitionMode competitionMode) {
        return switch (competitionMode) {
            case ATTACK_DEFENSE -> attackDefensePointsCounter;
            case REVERSE_DEFENSE -> reverseDefensePointsCounter;
        };
    }
}
