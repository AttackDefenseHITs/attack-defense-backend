package ru.hits.attackdefenceplatform.websocket.storage;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketStorage {
    private static final Map<SessionKey, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void add(final SessionKey sessionKey, final WebSocketSession session) {
        sessions.put(sessionKey, session);
    }

    public void sendMessage(final SessionKey sessionKey, final String message) {
        WebSocketSession session = sessions.get(sessionKey);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                throw new RuntimeException("exp");
            }
        }
    }

    public void remove(@NotNull final WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
            sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
        } catch (IOException e) {
            throw new RuntimeException("exp");
        }
    }

    public Set<SessionKey> getAllSessionKeys() {
        return sessions.keySet();
    }

    public Set<SessionKey> getSessionKeysByHandlerType(WebSocketHandlerType type) {
        return sessions.keySet().stream()
                .filter(key -> key.getWebSocketHandlerType() == type)
                .collect(Collectors.toSet());
    }

    public Set<SessionKey> getSessionKeysByRoleAndHandler(String role, WebSocketHandlerType type) {
        return sessions.keySet().stream()
                .filter(key -> role.equalsIgnoreCase(key.getRole()))
                .filter(key -> key.getWebSocketHandlerType() == type)
                .collect(Collectors.toSet());
    }
}
