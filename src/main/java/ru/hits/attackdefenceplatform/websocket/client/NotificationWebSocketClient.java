package ru.hits.attackdefenceplatform.websocket.client;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.websocket.model.EventModel;
import ru.hits.attackdefenceplatform.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationWebSocketClient {
    private final WebSocketStorage webSocketStorage;
    private final Gson gson = new Gson();

    public void sendNotificationToParticipants(EventModel event, List<String> participantIds) {
        for (String userId : participantIds) {
            var sessionKey = new SessionKey(userId, WebSocketHandlerType.COMPETITION);
            var message = gson.toJson(event);
            webSocketStorage.sendMessage(sessionKey, message);
        }
    }
}
