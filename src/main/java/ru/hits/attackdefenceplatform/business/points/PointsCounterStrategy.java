package ru.hits.attackdefenceplatform.business.points;

import java.util.UUID;

public interface PointsCounterStrategy {
    Double getTeamPoints(UUID teamId);
}
