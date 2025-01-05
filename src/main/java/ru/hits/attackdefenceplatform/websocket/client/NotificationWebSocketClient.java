package ru.hits.attackdefenceplatform.websocket.client;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.websocket.model.NotificationEventModel;
import ru.hits.attackdefenceplatform.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketClient implements WebSocketClient<NotificationEventModel> {
    private final WebSocketStorage webSocketStorage;
    private final Gson gson;

    @Override
    public void sendNotification(NotificationEventModel data, List<String> userIds) {
        for (var userId : userIds) {
            var sessionKey = new SessionKey(userId, WebSocketHandlerType.COMPETITION);
            var message = gson.toJson(data);
            webSocketStorage.sendMessage(sessionKey, message);
        }
    }
}
