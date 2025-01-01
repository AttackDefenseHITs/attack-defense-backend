package ru.hits.attackdefenceplatform.websocket.storage.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class SessionKey {
    private String userId;
    private WebSocketHandlerType webSocketHandlerType;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SessionKey s) {
            return userId.equals(s.userId) && webSocketHandlerType.equals(s.webSocketHandlerType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (userId + webSocketHandlerType).hashCode();
    }
}
