package ru.hits.attackdefenceplatform.business.competition.round;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.checker.CheckerExecutionService;
import ru.hits.attackdefenceplatform.publisher.RoundStartedEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoundEventListener {

    private final EventBus eventBus;
    private final CheckerExecutionService checkerExecutionService;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRoundStarted(RoundStartedEvent event) {
        log.info("Обработчик события: начался новый раунд №{}", event.roundNumber());

        if (event.roundNumber() > 0) {
            try {
                checkerExecutionService.runAllCheckers(List.of("check", "put", "get", "get_flags"));
                log.info("Чекеры запущены для раунда №{}", event.roundNumber());
            } catch (Exception e) {
                log.error("Ошибка при запуске чекеров для раунда №{}: {}", event.roundNumber(), e.getMessage(), e);
            }
        } else {
            log.info("Раунд №0 — пропуск запуска чекеров");
        }
    }
}
