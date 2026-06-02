package ru.hits.attackdefenceplatform.websocket.model;

import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

public record CheckerStatusEventModel(
        WebSocketHandlerType eventType,
        ServiceStatusInfo message
) { }
