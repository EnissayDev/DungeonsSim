package org.enissay.dungeonssim.commands.dungeonloc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.enissay.dungeonssim.dungeon.templates.RoomLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TempDungeonBuildsManager {
    private static Map<UUID, TempDungeonBuilds> map = new HashMap<>();

    public static void put(final UUID uuid, final TempDungeonBuilds tempDungeonBuilds) {
        map.put(uuid, tempDungeonBuilds);
    }

    public static void clear(final UUID uuid) {
        map.put(uuid, null);
    }

    public static TempDungeonBuilds get(final UUID uuid) {
        return map.get(uuid);
    }

    public static Location roomLocationToBukkit(final RoomLocation roomLocation) {
        return new Location(Bukkit.getWorld("world"), roomLocation.getX(), roomLocation.getY(), roomLocation.getZ());
    }
}
