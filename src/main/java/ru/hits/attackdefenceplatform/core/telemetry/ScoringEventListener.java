package ru.hits.attackdefenceplatform.core.telemetry;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.publisher.CompetitionResetEvent;
import ru.hits.attackdefenceplatform.publisher.FlagSubmittedEvent;

@Component
@RequiredArgsConstructor
public class ScoringEventListener {

    private final ScoringMetricsStore metrics;
    private final EventBus eventBus;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onFlagSubmitted(FlagSubmittedEvent event) {
        if (!event.correct()) return;

        metrics.recordFlagCapture(event.teamId(), event.serviceId(), event.round());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onCompetitionReset(CompetitionResetEvent event) {
        metrics.clearAll();
    }
}

