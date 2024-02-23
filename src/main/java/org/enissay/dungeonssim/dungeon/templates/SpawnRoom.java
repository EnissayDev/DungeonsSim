package org.enissay.dungeonssim.dungeon.templates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.DungeonType;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.HashMap;
import java.util.Map;

public class SpawnRoom implements DungeonTemplate {
    @Override
    public String getName() {
        return "SPAWN_ROOM";
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public DungeonType getType() {
        return DungeonType.SAFE;
    }

    /*@Override
    public Map<Cuboid, Double> getBuilds() {
        final Map<Cuboid, Double> map = new HashMap<>();
        final Cuboid room1 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 33),
                new Location(Bukkit.getWorld("world"), -22, 72, 18));

        map.put(room1, 100.0);
        return map;
    }*/

    @Override
    public Map<Entity, Double> getMonstersFrequency() {
        return null;
    }
}
