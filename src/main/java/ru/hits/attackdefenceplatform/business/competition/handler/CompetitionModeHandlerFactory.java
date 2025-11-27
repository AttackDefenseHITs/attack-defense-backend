package ru.hits.attackdefenceplatform.business.competition.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionMode;

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
