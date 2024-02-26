package org.enissay.dungeonssim.dungeon;

import org.bukkit.event.Listener;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.LinkedList;
import java.util.UUID;

public class DungeonRoom {

    private Dungeon dungeon;
    private DungeonTemplate template;
    private String roomName;
    private Cuboid cuboid;
    private int ID;
    private DungeonGeneration.GridCell gridCell;
    private LinkedList<UUID> watchers = new LinkedList<UUID>();

    public DungeonRoom(Dungeon dungeon, DungeonTemplate template, String roomName, Cuboid cuboid, DungeonGeneration.GridCell gridCell, int ID) {
        this.dungeon = dungeon;
        this.template = template;
        this.roomName = roomName;
        this.cuboid = cuboid;
        this.gridCell = gridCell;
        this.ID = ID;
    }

    public void addWatcher(final UUID uuid) {
        if (!watchers.contains(uuid)) watchers.add(uuid);
    }

    public LinkedList<UUID> getWatchers() {
        return watchers;
    }

    public DungeonGeneration.GridCell getGridCell() {
        return gridCell;
    }

    public void setGridCell(DungeonGeneration.GridCell gridCell) {
        this.gridCell = gridCell;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public DungeonTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DungeonTemplate template) {
        this.template = template;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
