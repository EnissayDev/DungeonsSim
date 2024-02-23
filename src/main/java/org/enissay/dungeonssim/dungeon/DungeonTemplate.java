package org.enissay.dungeonssim.dungeon;

import org.bukkit.entity.Entity;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuildsManager;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DungeonTemplate {

    String getName();
    default DungeonType getType() {
        return DungeonType.HOSTILE;
    }
    int getID();

    default Map<String, Double> getRoomsFromTemplate() {
        final List<TempDungeonBuilds> tempDungeonBuilds = DungeonHandler.loadRooms(getName());
        final Map<String, Double> rooms = new HashMap<>();
        tempDungeonBuilds.forEach(temp -> {
            rooms.put(temp.getName(), temp.getChance());
        });
        return rooms;
    }
    default Map<Cuboid, Double> getBuilds() {
        final List<TempDungeonBuilds> tempDungeonBuilds = DungeonHandler.loadRooms(getName());
        final Map<Cuboid, Double> builds = new HashMap<>();
        tempDungeonBuilds.forEach(temp -> {
            builds.put(new Cuboid(TempDungeonBuildsManager.roomLocationToBukkit(temp.getLocation1()),
                    TempDungeonBuildsManager.roomLocationToBukkit(temp.getLocation2())), temp.getChance());
        });
        return builds;
    } //Build/Chance of appearing
    Map<Entity, Double> getMonstersFrequency(); //Monster and his chance of spawning in that specific room
}
