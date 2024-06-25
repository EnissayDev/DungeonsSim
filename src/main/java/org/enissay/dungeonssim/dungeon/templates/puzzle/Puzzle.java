package org.enissay.dungeonssim.dungeon.templates.puzzle;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.handlers.PuzzleHandler;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Puzzle {

    private int ID;
    private Location location;
    private DungeonRoom dungeonRoom;
    private PuzzleType puzzleType;
    private BiConsumer<Player, Puzzle> callback;

    public Puzzle(int ID, Location location, DungeonRoom dungeonRoom, PuzzleType puzzleType) {
        this.ID = ID;
        this.location = location;
        this.dungeonRoom = dungeonRoom;
        this.puzzleType = puzzleType;
    }

    public PuzzleType getPuzzleType() {
        return puzzleType;
    }

    public void setPuzzleType(PuzzleType puzzleType) {
        this.puzzleType = puzzleType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public DungeonRoom getDungeonRoom() {
        return dungeonRoom;
    }

    public void setDungeonRoom(DungeonRoom dungeonRoom) {
        this.dungeonRoom = dungeonRoom;
    }

    public void complete(Player completer) {
        if (!dungeonRoom.getDungeon().isPuzzleCompleted(this)) {
            callback.accept(completer, this);
            dungeonRoom.getDungeon().addCompletedPuzzle(this);
            PuzzleHandler.getPuzzleTemplate(getPuzzleType()).onCompletion(dungeonRoom);
        }
    }

    public Puzzle whenComplete(BiConsumer<Player, Puzzle> callback) {
        this.callback = callback;
        return this;
    }
}
