package ru.hits.attackdefenceplatform.websocket.model;

import ru.hits.attackdefenceplatform.public_interface.deployment.DeploymentDataDto;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.util.List;

public record DeploymentEventModel(
        WebSocketHandlerType eventType,
        List<DeploymentDataDto> deploymentData
) {
}
