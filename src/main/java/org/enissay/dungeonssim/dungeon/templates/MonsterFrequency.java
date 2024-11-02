package org.enissay.dungeonssim.dungeon.templates;

import org.bukkit.Bukkit;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.entities.CustomMob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MonsterFrequency {
    private final Map<Class<? extends CustomMob>, List<DungeonTemplate.SpawnInfo>> frequencies;
    public MonsterFrequency() {
        this.frequencies = new HashMap<>();
    }

    public void addFrequency(Class<? extends CustomMob> monsterClass, DungeonTemplate template, String roomName, double chance) {
        List<DungeonTemplate.SpawnInfo> spawnInfos = frequencies.getOrDefault(monsterClass, new ArrayList<>());
        spawnInfos.add(new DungeonTemplate.SpawnInfo(roomName, chance, false, template));
        frequencies.put(monsterClass, spawnInfos);
    }

    public void addFrequency(Class<? extends CustomMob> monsterClass, DungeonTemplate template, String roomName, double chance, int spawnLimit) {
        List<DungeonTemplate.SpawnInfo> spawnInfos = frequencies.getOrDefault(monsterClass, new ArrayList<>());
        spawnInfos.add(new DungeonTemplate.SpawnInfo(roomName, chance, false, template, spawnLimit));
        frequencies.put(monsterClass, spawnInfos);
    }

    public void addFrequency(Class<? extends CustomMob> monsterClass, double chance, DungeonTemplate template) {
        List<DungeonTemplate.SpawnInfo> spawnInfos = frequencies.getOrDefault(monsterClass, new ArrayList<>());
        //spawnInfos.add(new DungeonTemplate.SpawnInfo(chance, template));
        template.getRoomsFromTemplate().forEach((roomName, roomChance) -> {
            spawnInfos.add(new DungeonTemplate.SpawnInfo(roomName, chance, true, template));
        });
        frequencies.put(monsterClass, spawnInfos);
    }

    public void addFrequency(Class<? extends CustomMob> monsterClass, double chance, DungeonTemplate template, int spawnLimit) {
        List<DungeonTemplate.SpawnInfo> spawnInfos = frequencies.getOrDefault(monsterClass, new ArrayList<>());
        //spawnInfos.add(new DungeonTemplate.SpawnInfo(chance, template));
        template.getRoomsFromTemplate().forEach((roomName, roomChance) -> {
            spawnInfos.add(new DungeonTemplate.SpawnInfo(roomName, chance, true, template, spawnLimit));
        });
        frequencies.put(monsterClass, spawnInfos);
    }

    public List<DungeonTemplate.SpawnInfo> getSpawnInfosForRoom(String roomName) {
        List<DungeonTemplate.SpawnInfo> result = new ArrayList<>();
        frequencies.values().forEach(infosList -> {
            infosList.forEach(spawnInfo -> {
                if (spawnInfo.getRoomName().equals(roomName)) result.add(spawnInfo);
            });
        });
        return result;
    }

    public double totalFrequency() {
        AtomicReference<Double> result = new AtomicReference<>((double) 0);
        frequencies.forEach((clazz, list) -> {
            result.set(list.stream()
                    .map(DungeonTemplate.SpawnInfo::getChance)
                    .mapToDouble(Double::doubleValue).sum());
        });
        return result.get();
    }

    public List<DungeonTemplate.SpawnInfo> getSpawnInfos(Class<? extends CustomMob> monsterClass) {
        List<DungeonTemplate.SpawnInfo> result = new ArrayList<>();
        List<DungeonTemplate.SpawnInfo> spawnInfos = frequencies.get(monsterClass);

        if (spawnInfos != null && spawnInfos.size() > 0) {
            for (DungeonTemplate.SpawnInfo spawnInfo : spawnInfos) {
                result.add(spawnInfo);
            }
        }
        return result;
    }

    public Class<? extends CustomMob> getMobFromSpawnInfo(DungeonTemplate.SpawnInfo spawnInfo) {
        for (Map.Entry<Class<? extends CustomMob>, List<DungeonTemplate.SpawnInfo>> entry : frequencies.entrySet()) {
            if (entry.getValue().contains(spawnInfo)) {
                return entry.getKey();
            }
        }
        return null;  // Return null if no matching SpawnInfo is found
    }

    public Map<Class<? extends CustomMob>, List<DungeonTemplate.SpawnInfo>> getFrequencies() {
        return frequencies;
    }

    @Override
    public String toString() {
        return "MonsterFrequency{" +
                "frequencies=" + frequencies +
                '}';
    }
}