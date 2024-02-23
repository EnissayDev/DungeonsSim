package org.enissay.dungeonssim.dungeon.grid;

public class GridCell {

    int x, y;
    int[] doors;//LEFT, RIGHT, DOWN, UP

    public GridCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDoors(int[] doors) {
        this.doors = doors;
    }

    public boolean hasLeftDoor() {
        return doors != null ? doors[0] == 1 : false;
    }

    public boolean hasRightDoor() {
        return doors != null ? doors[1] == 1 : false;
    }

    public boolean hasDownDoor() {
        return doors != null ? doors[2] == 1 : false;
    }

    public boolean hasUpDoor() {
        return doors != null ? doors[3] == 1 : false;
    }
}
