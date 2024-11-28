package ru.hits.attackdefenceplatform.core.websocket.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.core.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.core.websocket.storage.key.WebSocketHandlerType;

@Service
@RequiredArgsConstructor
public class WebSocketClient {
    private final WebSocketStorage webSocketStorage;

    public void sendEvent(String userId, String eventDto) {
        var sessionKey = new SessionKey(userId, WebSocketHandlerType.EVENT);
        webSocketStorage.sendMessage(sessionKey, eventDto);
    }
}
