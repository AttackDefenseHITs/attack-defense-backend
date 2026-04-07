package ru.hits.attackdefenceplatform.core.points.sla.metric;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TeamServiceSlaSnapshotKeys {

    public static String teamRound(UUID teamId, long roundNumber) {
        return "TEAM_SERVICE_SLA_SNAPSHOT:%s:%d".formatted(teamId, roundNumber);
    }

    public static String teamRoundPattern(UUID teamId, long roundNumber) {
        return "TEAM_SERVICE_SLA_SNAPSHOT:%s:%d".formatted(teamId, roundNumber);
    }
}
