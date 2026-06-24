package ru.hits.attackdefenceplatform.core.telemetry;

import java.util.UUID;

public class MetricKeys {
    public static String firstFlagRound(UUID serviceId, UUID teamId) {
        return "FIRST_FLAG_ROUND:%s:%s".formatted(serviceId, teamId);
    }

    public static String capturedTeams(UUID serviceId, int round) {
        return "CAPTURED_TEAMS:%s:%d".formatted(serviceId, round);
    }
}
