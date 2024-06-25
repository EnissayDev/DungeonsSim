package org.enissay.dungeonssim.profiles;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.handlers.ProfilesHandler;

public enum Rank {

    PLAYER(0, "Player", "", ChatColor.GRAY),
    MOD(1, "Moderator", "Moderator", ChatColor.AQUA),
    ADMIN(2, "Administator", "Admin", ChatColor.RED);

    private int power = 0;
    private String name, prefix;
    private ChatColor color;

    Rank(final int power, final String name, final String prefix, final ChatColor color){
        this.power = power;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
    }

    public static boolean isStaff(final Rank rank) {
        return rank.getPower() >= MOD.getPower();
    }

    public static boolean hasPermissionsOf(final Player player, final Rank rank) {
        return ProfilesHandler.findProfile(player.getUniqueId().toString()).getRank().getPower() >= rank.getPower();
    }

    public static boolean isStaff(final Player player) {
        return isStaff(ProfilesHandler.findProfile(player.getUniqueId().toString()).getRank());
    }

    public int getPower() {
        return power;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getColor() {
        return color;
    }

    public String fullPrefix() {
        return color + this.prefix;
    }
}