package ru.hits.attackdefenceplatform.business.telemetry;

import java.util.UUID;

public interface ScoringMetricsStore {

    void recordFlagCapture(UUID teamId, UUID serviceId, int round);

    int getRoundsWithoutFlag(UUID teamId, UUID serviceId, int currentRound);

    int getCaptureCount(UUID serviceId, int round);

    void clearAll();
}
