package org.enissay.dungeonssim.dungeon.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.profiles.DungeonPlayer;

public class DungeonFinishEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Dungeon dungeon;
    private final DungeonParty party;
    private final boolean success;

    public DungeonFinishEvent(Dungeon dungeon, DungeonParty party, boolean success) {
        this.dungeon = dungeon;
        this.party = party;
        this.success = success;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public DungeonParty getParty() {
        return party;
    }

    public boolean wasSuccessful() {
        return success;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}