package org.enissay.dungeonssim.entities.abilities.impl;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;

public class SpiderLeapAbility extends Ability {

    private final double leapStrength;

    public SpiderLeapAbility(long cooldown, double range, double leapStrength) {
        super(cooldown, range);
        this.leapStrength = leapStrength;
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        if (target == null || mob == null) return;

        Location mobLocation = mob.getBukkitEntity().getLocation();
        Location targetLocation = target.getBukkitEntity().getLocation();

        Vector direction = targetLocation.toVector().subtract(mobLocation.toVector()).normalize();

        Vector leap = direction.multiply(leapStrength).setY(0.4);
        mob.getBukkitEntity().setVelocity(leap);

        mob.getBukkitEntity().getWorld().playSound(mob.getBukkitEntity().getLocation(), Sound.ENTITY_SPIDER_STEP, 1.0f, 1.0f);
    }

    public double getLeapStrength() {
        return leapStrength;
    }
}