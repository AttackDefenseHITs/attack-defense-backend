package ru.hits.attackdefenceplatform.util;

import java.util.Random;

public class ColorUtils {
    public static String generateRandomColor() {
        Random random = new Random();
        return String.format("#%06x", random.nextInt(0xFFFFFF + 1));
    }
}
