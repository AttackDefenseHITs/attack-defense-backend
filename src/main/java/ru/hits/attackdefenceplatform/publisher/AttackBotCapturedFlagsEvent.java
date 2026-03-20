package ru.hits.attackdefenceplatform.publisher;

import java.util.List;
import java.util.UUID;

public record AttackBotCapturedFlagsEvent(
        UUID attackerTeamId,
        UUID attackerUserId,
        UUID targetTeamId,
        UUID serviceId,
        long round,
        List<String> flags
) {
}
