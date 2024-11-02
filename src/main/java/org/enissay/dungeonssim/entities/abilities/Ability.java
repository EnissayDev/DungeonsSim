package org.enissay.dungeonssim.entities.abilities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.ChatColor;
import org.enissay.dungeonssim.entities.AbstractCustomMob;

import java.util.List;

public abstract class Ability {

    private long cooldown;
    private long lastUsed;
    private double range;

    public Ability(long cooldown, double range) {
        this.cooldown = cooldown;
        this.lastUsed = 0;
        this.range = range;
    }

    public boolean isReady() {
        return System.currentTimeMillis() - lastUsed >= cooldown;
    }

    public void useAbility(AbstractCustomMob mob, LivingEntity target) {
        if (isReady()) {
            execute(mob, target);
            lastUsed = System.currentTimeMillis();
        }
    }

    protected abstract void execute(AbstractCustomMob mob, LivingEntity target);

    public long getCooldown() {
        return cooldown;
    }

    public double getRange() {
        return range;
    }
}