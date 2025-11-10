package ru.hits.attackdefenceplatform.core.points;

import java.util.UUID;

public interface PointsCounterStrategy {
    Double getTeamPoints(UUID teamId);
}
