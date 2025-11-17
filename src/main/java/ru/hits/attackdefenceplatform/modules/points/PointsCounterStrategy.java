package ru.hits.attackdefenceplatform.modules.points;

import java.util.UUID;

public interface PointsCounterStrategy {
    Double getTeamPoints(UUID teamId);
}
