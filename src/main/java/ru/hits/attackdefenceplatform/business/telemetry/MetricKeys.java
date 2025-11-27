package ru.hits.attackdefenceplatform.business.telemetry;

import java.util.UUID;

public class MetricKeys {
    public static String lastFlagRound(UUID serviceId, UUID teamId) {
        return "LAST_FLAG_ROUND:%s:%s".formatted(serviceId, teamId);
    }

    public static String capturedTeams(UUID serviceId, int round) {
        return "CAPTURED_TEAMS:%s:%d".formatted(serviceId, round);
    }
}
