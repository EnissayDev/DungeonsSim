package org.enissay.dungeonssim.dungeon;

import org.enissay.dungeonssim.dungeon.grid.GridCell;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class Dungeon {

    private int ID;
    private LinkedList<UUID> players;

    public Dungeon(int ID) {
        this.ID = ID;
        this.players = new LinkedList<>();
    }

    public int getID() {
        return ID;
    }

    public LinkedList<UUID> getPlayers() {
        return players;
    }
}
