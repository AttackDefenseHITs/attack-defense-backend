package ru.hits.attackdefenceplatform.websocket.storage.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SessionKey {
    private String userId;
    private String role;
    private WebSocketHandlerType webSocketHandlerType;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SessionKey s)) return false;

        return Objects.equals(userId, s.userId)
                && Objects.equals(role, s.role)
                && webSocketHandlerType == s.webSocketHandlerType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, role, webSocketHandlerType);
    }
}

