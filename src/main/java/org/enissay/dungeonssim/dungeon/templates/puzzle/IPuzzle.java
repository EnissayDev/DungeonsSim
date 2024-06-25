package org.enissay.dungeonssim.dungeon.templates.puzzle;

import org.enissay.dungeonssim.dungeon.system.DungeonRoom;

public interface IPuzzle {

    PuzzleType getType();

    void initEvents(final DungeonRoom room);

    void onCompletion(final DungeonRoom room);
}
