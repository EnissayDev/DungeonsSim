package org.enissay.dungeonssim.dungeon.templates.puzzle;

public enum PuzzleType {

    CUBE_ROTATION("Cube rotation"),
    SOUL_COLLECTING("Soul Collecting");

    private String name;

    PuzzleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
