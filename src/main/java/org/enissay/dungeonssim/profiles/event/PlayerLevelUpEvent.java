package org.enissay.dungeonssim.profiles.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.profiles.DungeonPlayer;

public class PlayerLevelUpEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final DungeonPlayer dungeonPlayer;
    private final int newLevel, oldLevel;

    public PlayerLevelUpEvent(Player player, DungeonPlayer dungeonPlayer, int newLevel, int oldLevel) {
        this.player = player;
        this.dungeonPlayer = dungeonPlayer;
        this.newLevel = newLevel;
        this.oldLevel = oldLevel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public DungeonPlayer getDungeonPlayer() {
        return dungeonPlayer;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
