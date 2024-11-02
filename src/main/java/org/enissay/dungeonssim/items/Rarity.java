package org.enissay.dungeonssim.items;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON(0, ChatColor.WHITE),
    UNCOMMON(1, ChatColor.GRAY),
    RARE(2, ChatColor.BLUE),
    SCATTERED(3, ChatColor.DARK_AQUA),
    LEGENDARY(4, ChatColor.GOLD),
    MYTHIC(5, ChatColor.LIGHT_PURPLE);

    private int id;
    private ChatColor color;

    Rarity(int id, ChatColor color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return getColor().toString() /*+ ChatColor.BOLD*/ + name();
    }
}