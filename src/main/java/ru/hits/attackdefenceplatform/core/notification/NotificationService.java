package ru.hits.attackdefenceplatform.core.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.websocket.client.WebSocketClient;
import ru.hits.attackdefenceplatform.websocket.model.NotificationEventModel;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final WebSocketClient<NotificationEventModel> webSocketClient;

    public void notifyAllTeams(String message) {
        var event = new NotificationEventModel(WebSocketHandlerType.EVENT, message);
        webSocketClient.sendNotification(event);
    }
}
