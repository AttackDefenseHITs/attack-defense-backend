package ru.hits.attackdefenceplatform.core.notification;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.publisher.CompetitionEvent;
import ru.hits.attackdefenceplatform.publisher.RoundStartedEvent;
import ru.hits.attackdefenceplatform.websocket.client.WebSocketClient;
import ru.hits.attackdefenceplatform.websocket.model.NotificationEventModel;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

@Service
@RequiredArgsConstructor
public class NotificationCompetitionEventListener {
    private final EventBus eventBus;
    private final WebSocketClient<NotificationEventModel> webSocketClient;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRoundStartedNotification(RoundStartedEvent event) {
        var eventModel = new NotificationEventModel(
                WebSocketHandlerType.EVENT,
                "Начался раунд " + event.roundNumber()
        );
        webSocketClient.sendNotification(eventModel);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onChangeCompetitionStatus(CompetitionEvent event) {
        var eventModel = new NotificationEventModel(
                WebSocketHandlerType.EVENT,
                event.message()
        );
        webSocketClient.sendNotification(eventModel);
    }
}
