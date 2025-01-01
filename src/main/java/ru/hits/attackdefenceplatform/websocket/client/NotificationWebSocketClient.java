package ru.hits.attackdefenceplatform.websocket.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationWebSocketClient {
    private final WebSocketStorage webSocketStorage;

    public void sendNotificationToParticipants(String event, String message, List<String> participantIds) {
        for (String userId : participantIds) {
            var sessionKey = new SessionKey(userId, WebSocketHandlerType.COMPETITION);
            webSocketStorage.sendMessage(sessionKey, buildMessage(event, message));
        }
    }

    private String buildMessage(String event, String message) {
        return String.format("{\"event\":\"%s\", \"message\":\"%s\"}", event, message);
    }
}
