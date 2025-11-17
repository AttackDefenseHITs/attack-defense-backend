package ru.hits.attackdefenceplatform.modules.points;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;

@Component
@RequiredArgsConstructor
public class PointsCounterStrategyFactory {
    private final AttackDefensePointsCounter attackDefensePointsCounter;
    private final ReverseDefensePointsCounter reverseDefensePointsCounter;
    private final DynamicAttackDefensePointsCounter dynamicDefensePointsCounter;

    public PointsCounterStrategy getStrategy(CompetitionMode competitionMode) {
        return switch (competitionMode) {
            case ATTACK_DEFENSE -> dynamicDefensePointsCounter;
            case REVERSE_DEFENSE -> reverseDefensePointsCounter;
        };
    }
}
