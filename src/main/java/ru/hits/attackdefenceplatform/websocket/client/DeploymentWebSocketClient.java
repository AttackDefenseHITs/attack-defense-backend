package ru.hits.attackdefenceplatform.websocket.client;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.public_interface.deployment.DeploymentResult;
import ru.hits.attackdefenceplatform.websocket.model.DeploymentEventModel;
import ru.hits.attackdefenceplatform.websocket.storage.WebSocketStorage;
import ru.hits.attackdefenceplatform.websocket.storage.key.SessionKey;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentWebSocketClient implements WebSocketClient<DeploymentResult> {
    private final WebSocketStorage webSocketStorage;
    private final Gson gson;

    @Override
    public void sendNotification(DeploymentResult data, List<String> userIds) {
        for (var userId : userIds) {
            var sessionKey = new SessionKey(userId, WebSocketHandlerType.DEPLOYMENT_UPDATE);
            var newData = new DeploymentEventModel(WebSocketHandlerType.DEPLOYMENT_UPDATE, data.deploymentData());
            var message = gson.toJson(newData);
            webSocketStorage.sendMessage(sessionKey, message);
        }
    }
}

