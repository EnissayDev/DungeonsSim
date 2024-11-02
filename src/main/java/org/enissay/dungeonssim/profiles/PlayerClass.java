package org.enissay.dungeonssim.profiles;

import org.bukkit.ChatColor;

public enum PlayerClass {

    MAGE("Mage", ChatColor.LIGHT_PURPLE),
    WARRIOR("Warrior", ChatColor.RED),
    TANK("Tank", ChatColor.GRAY),
    ARCHER("Archer", ChatColor.GREEN),
    NONE("None", ChatColor.WHITE);

    private String name;
    private ChatColor color;

    PlayerClass(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getText() {
        return color + name;
    }
}
