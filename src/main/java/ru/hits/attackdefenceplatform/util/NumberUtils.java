package ru.hits.attackdefenceplatform.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    public static double roundToThreeDecimals(double number) {
        return new BigDecimal(number)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
