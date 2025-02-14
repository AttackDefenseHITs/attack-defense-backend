package ru.hits.attackdefenceplatform.websocket.handler;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventHandler extends AbstractWebSocketHandler {
    private final JwtTokenUtils jwtTokenUtils;

    protected String getUserId(WebSocketSession session) {
        var query = session.getUri().getQuery();
        if (query != null && query.contains("token")) {
            var token = Arrays.stream(query.split("&"))
                    .filter(param -> param.startsWith("token="))
                    .map(param -> param.substring(6))
                    .findFirst()
                    .orElse(null);

            if (token != null) {
                try {
                    return jwtTokenUtils.getUserIdFromToken(token).toString();
                } catch (ExpiredJwtException ex) {
                    log.error("JWT токен истёк: {}. Закрываем WebSocket-сессию {}", ex.getMessage(), session.getId());
                    closeSession(session);
                } catch (Exception ex) {
                    log.error("Ошибка при разборе JWT токена: {}. Закрываем WebSocket-сессию {}", ex.getMessage(), session.getId());
                    closeSession(session);
                }
            }
        }

        log.error("Некорректный или отсутствующий токен. Закрываем WebSocket-сессию {}", session.getId());
        closeSession(session);
        return "";
    }

    private void closeSession(WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            log.error("Ошибка при закрытии WebSocket-сессии {}: {}", session.getId(), e.getMessage());
        }
    }
}

