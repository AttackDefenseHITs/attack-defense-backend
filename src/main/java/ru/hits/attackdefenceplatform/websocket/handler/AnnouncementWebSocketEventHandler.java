package ru.hits.attackdefenceplatform.websocket.handler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import ru.hits.attackdefenceplatform.common.util.JwtTokenUtils;
import ru.hits.attackdefenceplatform.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.io.IOException;

@Component
@Slf4j
public class AnnouncementWebSocketEventHandler extends AbstractWebSocketEventHandler {
    private final WebSocketStorage webSocketStorage;

    public AnnouncementWebSocketEventHandler(JwtTokenUtils jwtTokenUtils, WebSocketStorage webSocketStorage) {
        super(jwtTokenUtils);
        this.webSocketStorage = webSocketStorage;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            var userData = getUserData(session);
            if (userData == null) {
                return;
            }
            SessionKey sessionKey = new SessionKey(userData.userId(), userData.role(), WebSocketHandlerType.EVENT);
            webSocketStorage.add(sessionKey, session);
        } catch (Exception ex) {
            log.error("Ошибка при установлении WebSocket соединения: {}", ex.getMessage(), ex);
            try {
                session.close();
            } catch (IOException e) {
                log.error("Ошибка при закрытии WebSocket сессии: {}", e.getMessage(), e);
            }
        }
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


