package ru.hits.attackdefenceplatform.core.competition.round;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.competition.mode.CompetitionModeRegistry;
import ru.hits.attackdefenceplatform.modules.checker.CheckerExecutionService;
import ru.hits.attackdefenceplatform.publisher.RoundStartedEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoundEventListener {

    private final EventBus eventBus;
    private final CompetitionContext competitionContext;
    private final CompetitionModeRegistry modeRegistry;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRoundStarted(RoundStartedEvent event) {
        int round = event.roundNumber();
        log.info("Начался новый раунд №{}", round);

        var competition = competitionContext.getCurrent();
        var module = modeRegistry.getModule(competition.getCompetitionMode());
        try {
            module.roundPolicy().onRoundStarted(competition, round);
        } catch (Exception e) {
            log.error("Ошибка при обработке старта раунда №{} в режиме {}: {}",
                    round, competition.getCompetitionMode(), e.getMessage(), e);
        }
    }
}

