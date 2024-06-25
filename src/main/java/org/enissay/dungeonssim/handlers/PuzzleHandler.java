package org.enissay.dungeonssim.handlers;

import org.bukkit.Location;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.templates.puzzle.IPuzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PuzzleHandler {
    private static LinkedList<Puzzle> puzzles = new LinkedList<>();
    private static LinkedList<IPuzzle> puzzleTemplates = new LinkedList<>();

    public static void addPuzzleTemplates(final IPuzzle... puzzles) {
        Arrays.asList(puzzles).forEach(puzz -> {
            puzzleTemplates.add(puzz);
            System.out.println("Added puzzle " + puzz.getType().name() + " size: " + getPuzzleTemplates().size());
        });
    }

    public static LinkedList<IPuzzle> getPuzzleTemplates() {
        return puzzleTemplates;
    }

    public static IPuzzle getPuzzleTemplate(final PuzzleType type) {
        return puzzleTemplates.stream().filter(iPuzzle -> iPuzzle != null && iPuzzle.getType() == type).findFirst().orElse(null);
    }

    public static Puzzle getPuzzle(final DungeonRoom dungeonRoom) {
        return puzzles.stream().filter(puzzle -> puzzle.getDungeonRoom() != null && puzzle.getDungeonRoom() == dungeonRoom).findFirst().orElse(null);
    }

    public static List<Puzzle> getPuzzles(final Dungeon dungeon) {
        return puzzles.stream().filter(puzzle -> puzzle.getDungeonRoom() != null && puzzle.getDungeonRoom().getDungeon() == dungeon).collect(Collectors.toList());
    }

    public static void addPuzzle(final Location location, final DungeonRoom dungeonRoom, final PuzzleType puzzleType) {
        final Puzzle puzzle = new Puzzle(puzzles.size() + 1, location, dungeonRoom, puzzleType);
        addPuzzle(puzzle);
    }

    public static void addPuzzle(final Puzzle puzzle) {
        if (!puzzles.contains(puzzle)) puzzles.add(puzzle);
    }

    public static void resetPuzzles(final Dungeon dungeon) {
        /*LinkedList<Puzzle> puzzlesToRemove = new LinkedList<>();
        puzzles.forEach(puzzle -> {
            if (puzzle.getDungeonRoom().getDungeon() == dungeon) {
                puzzlesToRemove.add(puzzle);
            }
        });
        puzzles.removeAll(puzzlesToRemove);*/
        puzzles.removeAll(getPuzzles(dungeon));
    }

    public static void register(final IPuzzle... puzzles) {
        addPuzzleTemplates(puzzles);
    }
}
