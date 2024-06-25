package org.enissay.dungeonssim.dungeon.system;

import org.bukkit.Location;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuildsManager;
import org.enissay.dungeonssim.dungeon.CuboidTest;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.templates.RoomLocation;
import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleType;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PuzzleHandler;
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

    public void addPuzzle(final Location location, final PuzzleType puzzleType) {
        PuzzleHandler.addPuzzle(location, this, puzzleType);
    }

    public Puzzle getPuzzle() {
        return PuzzleHandler.getPuzzle(this);
    }

    public Location getLocationFromTemplate(String locationName) {
        Location location = null;
        final TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(this.getTemplate().getName(), this.getRoomName());
        if (tempDungeonBuilds.getRoomLocations().get(locationName) != null && this.cuboid != null) {
            final RoomLocation roomLocation = tempDungeonBuilds.getRoomLocations().get(locationName);
            final CuboidTest cuboid1 = new CuboidTest(this.getCuboid().getPoint1(), this.getCuboid().getPoint2());
            final CuboidTest cuboid2 = new CuboidTest(TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation1()), TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation2()));
            final double finalX = (cuboid1.getLowerX() - cuboid2.getLowerX()) + roomLocation.getX();
            final double finalY = (cuboid1.getLowerY() - cuboid2.getLowerY()) + roomLocation.getY();
            final double finalZ = (cuboid1.getLowerZ() - cuboid2.getLowerZ()) + roomLocation.getZ();

            location = new Location(cuboid1.getWorld(), finalX, finalY, finalZ);
        }
        return location;

    }
}
