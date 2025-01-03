package ru.hits.attackdefenceplatform.websocket.client;

import java.util.List;

public interface WebSocketClient<T> {
    void sendNotification(T data, List<String> userIds);
}

