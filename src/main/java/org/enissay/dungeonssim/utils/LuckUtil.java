package org.enissay.dungeonssim.utils;

import java.util.Map;

public class LuckUtil {

    public static <E> E getRandomWeighted(Map<E, ? extends Number> balancedObjects) throws IllegalArgumentException {
        double totalWeight = balancedObjects.values().stream().mapToInt(Number::intValue).sum(); // Java 8

        if (totalWeight <= 0)
            throw new IllegalArgumentException("Total weight must be positive.");

        double value = Math.random()*totalWeight, weight = 0;

        for (Map.Entry<E, ? extends Number> e : balancedObjects.entrySet()) {
            weight += e.getValue().doubleValue();
            if (value < weight)
                return e.getKey();
        }

        return null; // Never will reach this point
    }
}
