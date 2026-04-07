package ru.hits.attackdefenceplatform.core.dashboard.metric;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DashboardMetricKeys {

    public static String roundSnapshot(long roundNumber) {
        return "DASHBOARD_ROUND_SNAPSHOT:%d".formatted(roundNumber);
    }
}
