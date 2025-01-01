package ru.hits.attackdefenceplatform.websocket.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;
import ru.hits.attackdefenceplatform.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
@Slf4j
public class CompetitionEventHandler extends AbstractWebSocketHandler {
    private final WebSocketStorage webSocketStorage;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        var userId = getUserId(session);
        SessionKey sessionKey = new SessionKey(userId, WebSocketHandlerType.COMPETITION);
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

    private String getUserId(WebSocketSession session) {
        var query = session.getUri().getQuery();
        if (query != null && query.contains("token")) {
            var token = Arrays.stream(query.split("&"))
                    .filter(param -> param.startsWith("token="))
                    .map(param -> param.substring(6))
                    .findFirst()
                    .orElse(null);
            if (token != null) {
                return jwtTokenUtils.getUserIdFromToken(token).toString();
            }
        }
        throw new RuntimeException("Invalid or missing token");
    }
}

