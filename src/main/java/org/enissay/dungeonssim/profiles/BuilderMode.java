package org.enissay.dungeonssim.profiles;


import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BuilderMode {

    public static Map<Player, Boolean> builderMode = new HashMap<>();

    public static void putStatus(final Player player, final boolean mode) {
        builderMode.put(player, mode);
    }

    public static boolean canBuild(final Player player) {
        return builderMode.get(player) == null ? false : builderMode.get(player);
    }
}