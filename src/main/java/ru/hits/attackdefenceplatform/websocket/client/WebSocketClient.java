package ru.hits.attackdefenceplatform.websocket.client;

public interface WebSocketClient<T> {
    void sendNotification(T data);
}

