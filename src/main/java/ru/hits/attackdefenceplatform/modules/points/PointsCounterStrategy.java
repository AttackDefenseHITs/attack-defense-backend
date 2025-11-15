package ru.hits.attackdefenceplatform.modules.points;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public abstract class PointsCounterStrategy {
    abstract public Double getTeamPoints(UUID teamId);

    protected Double roundToThreeDecimals(double value) {
        return new BigDecimal(value)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
