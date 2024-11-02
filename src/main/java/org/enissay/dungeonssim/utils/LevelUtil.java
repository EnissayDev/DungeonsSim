package org.enissay.dungeonssim.utils;

public class LevelUtil {
    private static final double BASE_EXP = 1000;
    private static final double GROWTH_FACTOR = 1.3;

    // Calculates the maximum EXP required for a given level.
    public static long maxExpForLevel(int level) {
        if (level <= 0) {
            return 0;  // There should be no level 0
        }
        return Math.round(BASE_EXP * Math.pow(GROWTH_FACTOR, level - 1));
    }

    // Calculates the minimum EXP required for a given level.
    public static long minExpForLevel(int level) {
        if (level <= 1) {
            return 0;  // Level 1 starts at 0 EXP
        }
        return Math.round(BASE_EXP * Math.pow(GROWTH_FACTOR, level - 2));
    }

    // Given the EXP, calculates the level.
    public static int expToLevel(long exp) {
        if (exp < 0) {
            return 1;  // Negative EXP doesn't make sense in this context
        }

        int level = 1;
        while (exp >= maxExpForLevel(level)) {
            level++;
        }
        return level;
    }

    // Given the current EXP, gets the maximum EXP for the next level.
    public static long getMaxEXP(long currentExp) {
        int level = expToLevel(currentExp);
        return maxExpForLevel(level);
    }

    // Given the current EXP, calculates the level.
    public static int getLevel(long exp) {
        return expToLevel(exp);
    }

    public static void main(String[] args) {
        // Testing levels from 1 to 10
        for (int i = 1; i <= 100; i++) {
            System.out.println("For level " + i + " -> Min EXP: " + minExpForLevel(i) + ", Max EXP: " + maxExpForLevel(i));
        }

        // Testing specific cases
        long exp = 250;
        int level = expToLevel(exp);
        System.out.println("Level for " + exp + " EXP: " + level);

        long maxExpForCurrentLevel = getMaxEXP(exp);
        System.out.println("Max EXP for current level: " + maxExpForCurrentLevel);

        int specificLevel = 5;
        long minExpForSpecificLevel = minExpForLevel(specificLevel);
        System.out.println("Min EXP for level " + specificLevel + ": " + minExpForSpecificLevel);
    }
}