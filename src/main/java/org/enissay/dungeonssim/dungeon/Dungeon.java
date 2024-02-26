package org.enissay.dungeonssim.dungeon;

import org.enissay.dungeonssim.utils.Cuboid;

import java.util.*;

public class Dungeon {

    private int ID;
    private List<UUID> players;
    private LinkedList<DungeonRoom> rooms;
    private Cuboid cuboid;

    public Dungeon(int ID, List<UUID> players) {
        this.ID = ID;
        this.players = players;
        this.rooms = new LinkedList<>();
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public void addRoom(final DungeonRoom room) {
        if (!rooms.contains(room)) rooms.add(room);
    }

    public LinkedList<DungeonRoom> getRooms() {
        return rooms;
    }

    public int getID() {
        return ID;
    }

    public List<UUID> getPlayers() {
        return players;
    }
}
