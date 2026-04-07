package ru.hits.attackdefenceplatform.core.dashboard.model;

import java.util.Date;
import java.util.UUID;

public record TeamRoundSnapshot(
        long roundNumber,
        UUID teamId,
        String teamName,
        String teamColor,
        Date snapshotTime,
        double totalPoints
) {
}
