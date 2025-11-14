package ru.hits.attackdefenceplatform.core.notification;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationRoundEventListener {
    private final EventBus eventBus;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }
}
