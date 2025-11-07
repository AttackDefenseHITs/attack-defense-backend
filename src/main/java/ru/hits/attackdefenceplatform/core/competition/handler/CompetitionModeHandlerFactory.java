package ru.hits.attackdefenceplatform.core.competition.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;

import static ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode.ATTACK_DEFENSE;
import static ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode.REVERSE_DEFENSE;

@Component
@RequiredArgsConstructor
public class CompetitionModeHandlerFactory {
    private final AttackDefenseHandler attackDefenseHandler;
    private final ReverseDefenseHandler reverseDefenseHandler;

    public CompetitionModeHandler getHandler(CompetitionMode mode) {
        return switch (mode) {
            case ATTACK_DEFENSE -> attackDefenseHandler;
            case REVERSE_DEFENSE -> reverseDefenseHandler;
        };
    }
}
