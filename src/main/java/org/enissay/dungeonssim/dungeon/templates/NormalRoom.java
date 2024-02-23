package org.enissay.dungeonssim.dungeon.templates;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.HashMap;
import java.util.Map;

public class NormalRoom implements DungeonTemplate {
    @Override
    public String getName() {
        return "NORMAL_ROOM";
    }

    @Override
    public int getID() {
        return 0;
    }

    /*@Override
    public Map<Cuboid, Double> getBuilds() {
        final Map<Cuboid, Double> map = new HashMap<>();
        final Cuboid room1 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 54),
                new Location(Bukkit.getWorld("world"), -22, 72, 39));
        final Cuboid room2 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 54),
                new Location(Bukkit.getWorld("world"), -22, 72, 39));

        map.put(room1, 50.0);
        map.put(room2, 50.0);
        return map;
    }*/


    @Override
    public Map<Entity, Double> getMonstersFrequency() {
        return null;
    }
}
