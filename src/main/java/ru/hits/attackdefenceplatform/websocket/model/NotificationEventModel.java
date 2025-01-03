package ru.hits.attackdefenceplatform.websocket.model;

import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

public record NotificationEventModel(
        WebSocketHandlerType eventType,
        String message
) {
}
