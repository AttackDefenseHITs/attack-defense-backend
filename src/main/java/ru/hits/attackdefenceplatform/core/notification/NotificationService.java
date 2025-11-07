package ru.hits.attackdefenceplatform.core.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.websocket.client.WebSocketClient;
import ru.hits.attackdefenceplatform.websocket.model.NotificationEventModel;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final WebSocketClient<NotificationEventModel> webSocketClient;
    private final TeamMemberRepository teamMemberRepository;

    public void notifyAllTeams(String message) {
        var ids = teamMemberRepository.findAllUserIds().stream()
                .map(UUID::toString)
                .toList();
        var event = new NotificationEventModel(WebSocketHandlerType.EVENT, message);
        webSocketClient.sendNotification(event, ids);
    }
}
