package ru.hits.attackdefenceplatform.common;

import com.google.common.eventbus.EventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final EventBus eventBus;

    public void publish(Object event) {
        eventBus.post(event);
    }
}

