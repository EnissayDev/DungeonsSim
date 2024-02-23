package org.enissay.dungeonssim.dungeon;

public enum RoomRotation {

    NORTH(0),
    EAST(90),
    SOUTH(180),
    WEST(270);

    private int rotationTheta;

    RoomRotation(int rotationTheta) {
        this.rotationTheta = rotationTheta;
    }

    public int getRotationTheta() {
        return rotationTheta;
    }

    public RoomRotation getOpposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case EAST:
                return WEST;
            case SOUTH:
                return NORTH;
            case WEST:
                return EAST;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
