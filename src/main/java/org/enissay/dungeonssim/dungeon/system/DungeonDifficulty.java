package org.enissay.dungeonssim.dungeon.system;

public enum DungeonDifficulty {

    EASY(1),
    NORMAL(10),
    HARD(30),
    NIGHTMARE(60);

    private int level;

    DungeonDifficulty(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
