package ru.hits.attackdefenceplatform.websocket.handler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;
import ru.hits.attackdefenceplatform.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

@Component
@Slf4j
public class CheckerStatusEventHandler extends AbstractEventHandler{
    private final WebSocketStorage webSocketStorage;

    public CheckerStatusEventHandler(JwtTokenUtils jwtTokenUtils, WebSocketStorage webSocketStorage) {
        super(jwtTokenUtils);
        this.webSocketStorage = webSocketStorage;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        var userId = getUserId(session);
        SessionKey sessionKey = new SessionKey(userId, WebSocketHandlerType.CHECKER);
        webSocketStorage.add(sessionKey, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        webSocketStorage.remove(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        webSocketStorage.remove(session);
    }
}
