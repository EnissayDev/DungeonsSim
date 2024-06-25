package org.enissay.dungeonssim.dungeon.system;

import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.utils.Cuboid;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Dungeon {

    private int ID;
    private List<UUID> players;
    private LinkedList<DungeonRoom> rooms;
    private Cuboid cuboid;
    private DungeonDifficulty dungeonDifficulty;
    private DungeonGeneration dungeonGeneration;
    private LinkedList<DungeonRoom> roomsOpened;
    private Instant timeStarted;

    private LinkedList<Puzzle> completedPuzzles;

    public Dungeon(int ID, List<UUID> players, DungeonDifficulty dungeonDifficulty, Instant timeStarted) {
        this.ID = ID;
        this.players = players;
        this.rooms = new LinkedList<>();
        this.dungeonDifficulty = dungeonDifficulty;
        this.roomsOpened = new LinkedList<>();
        this.completedPuzzles = new LinkedList<>();
        this.timeStarted = timeStarted;
    }

    public LinkedList<Puzzle> getCompletedPuzzles() {
        return completedPuzzles;
    }

    public void addCompletedPuzzle(final Puzzle puzzle) {
        if (!completedPuzzles.contains(puzzle)) completedPuzzles.add(puzzle);
    }

    public boolean isPuzzleCompleted(final Puzzle puzzle) {
        return completedPuzzles.contains(puzzle);
    }

    public Instant getTimeStarted() {
        return timeStarted;
    }

    public long getTimeElapsed() {
        return Duration.between(timeStarted, Instant.now()).toMillis();
    }

    public String getTime() {
        int minutes = (int) (getTimeElapsed() / (60 * 1000));
        int seconds = (int) ((getTimeElapsed() / 1000) % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public void setTimeStarted(Instant timeStarted) {
        this.timeStarted = timeStarted;
    }

    public LinkedList<DungeonRoom> getRoomsOpened() {
        return roomsOpened;
    }

    public void addRoomOpened(final DungeonRoom dungeonRoom) {
        if (this.rooms.contains(dungeonRoom) && !this.roomsOpened.contains(dungeonRoom))
            roomsOpened.add(dungeonRoom);
    }

    public Cuboid getCuboid() {
        return cuboid;
    }


    public DungeonGeneration getDungeonGeneration() {
        return dungeonGeneration;
    }

    public void setDungeonGeneration(DungeonGeneration dungeonGeneration) {
        this.dungeonGeneration = dungeonGeneration;
    }

    public DungeonDifficulty getDungeonDifficulty() {
        return dungeonDifficulty;
    }

    public void setDungeonDifficulty(DungeonDifficulty dungeonDifficulty) {
        this.dungeonDifficulty = dungeonDifficulty;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public void addRoom(final DungeonRoom room) {
        if (!rooms.contains(room)) rooms.add(room);
    }

    public void addPlayer(final UUID uuid) {
        final List<UUID> players = new ArrayList<>(this.players);
        if (!getPlayers().contains(uuid)) players.add(uuid);
        this.players = players;
    }

    public void removePlayer(final UUID uuid) {
        final List<UUID> players = new ArrayList<>(this.players);
        if (getPlayers().contains(uuid)) {
            rooms.forEach(room -> {
                if (room.getWatchers().contains(uuid)) {
                    room.getWatchers().remove(uuid);
                }
            });
            players.remove(uuid);
        }
        this.players = players;
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
