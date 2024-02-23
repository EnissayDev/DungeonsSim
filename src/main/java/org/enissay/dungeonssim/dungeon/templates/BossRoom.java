package org.enissay.dungeonssim.dungeon.templates;

import org.bukkit.entity.Entity;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.DungeonType;

import java.util.Map;

public class BossRoom implements DungeonTemplate {
    @Override
    public String getName() {
        return "BOSS_ROOM";
    }

    @Override
    public DungeonType getType() {
        return DungeonType.HOSTILE;
    }

    @Override
    public int getID() {
        return 3;
    }

    @Override
    public Map<Entity, Double> getMonstersFrequency() {
        return null;
    }
}
