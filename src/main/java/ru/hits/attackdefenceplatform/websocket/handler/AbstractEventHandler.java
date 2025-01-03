package ru.hits.attackdefenceplatform.websocket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;

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
                return jwtTokenUtils.getUserIdFromToken(token).toString();
            }
        }
        throw new RuntimeException("Invalid or missing token");
    }
}
