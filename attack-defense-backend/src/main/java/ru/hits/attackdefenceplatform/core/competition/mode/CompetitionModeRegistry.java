package ru.hits.attackdefenceplatform.core.competition.mode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompetitionModeRegistry {

    private final List<CompetitionModeModule> modules;

    public CompetitionModeModule getModule(CompetitionMode mode) {
        return modules.stream()
                .filter(m -> m.mode() == mode)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No module for mode " + mode));
    }
}
