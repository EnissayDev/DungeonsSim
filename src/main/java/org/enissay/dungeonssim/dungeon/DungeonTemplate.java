package org.enissay.dungeonssim.dungeon;

import org.bukkit.entity.Entity;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuildsManager;
import org.enissay.dungeonssim.dungeon.system.DungeonType;
import org.enissay.dungeonssim.dungeon.templates.MonsterFrequency;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface DungeonTemplate{

    String getName();
    default DungeonType getType() {
        return DungeonType.HOSTILE;
    }
    int getID();

    default boolean hasUniqueRooms() {
        return false;
    }

    int getMinRooms();
    int getMaxRooms();

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
    }

    void initEvents();

    //Build/Chance of appearing
    MonsterFrequency getMonstersFrequency(); //Monster and his chance of spawning in that specific room

    public class SpawnInfo {
        private final String roomName;
        private final double chance;
        private final boolean global;
        private DungeonTemplate template;
        private int spawnLimit;

        public SpawnInfo(String roomName, double chance, boolean global, DungeonTemplate template, int spawnLimit) {
            this.roomName = roomName;
            this.chance = chance;
            this.global = global;
            this.template = template;
            this.spawnLimit = spawnLimit;
        }

        public SpawnInfo(String roomName, double chance, boolean global, DungeonTemplate template) {
            this.roomName = roomName;
            this.chance = chance;
            this.global = global;
            this.template = template;
            this.spawnLimit = -1;
        }

        public int getSpawnLimit() {
            return spawnLimit;
        }

        public String getRoomName() {
            return roomName;
        }

        public double getChance() {
            return chance;
        }

        public boolean isGlobal() {
            return global;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpawnInfo spawnInfo = (SpawnInfo) o;
            return Double.compare(spawnInfo.chance, chance) == 0 &&
                    global == spawnInfo.global &&
                    roomName.equals(spawnInfo.roomName) &&
                    template.equals(spawnInfo.template) &&
                    spawnInfo.spawnLimit == spawnLimit;
        }

        @Override
        public int hashCode() {
            return Objects.hash(roomName, chance, global, template, spawnLimit);
        }

        @Override
        public String toString() {
            return "SpawnInfo{" +
                    "roomName='" + roomName + '\'' +
                    ", chance=" + chance +
                    ", global=" + global +
                    '}';
        }
    }
}
