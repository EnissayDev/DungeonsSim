package org.enissay.dungeonssim.entities.abilities.impl;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;

public class SpiderLeapAbility extends Ability {

    private final double leapStrength; // Strength of the leap

    public SpiderLeapAbility(long cooldown, double range, double leapStrength) {
        super(cooldown, range);
        this.leapStrength = leapStrength;
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        if (target == null || mob == null) return;

        // Get the spider's location
        Location mobLocation = mob.getBukkitEntity().getLocation();
        Location targetLocation = target.getBukkitEntity().getLocation();

        // Calculate the direction vector from the spider to the target
        Vector direction = targetLocation.toVector().subtract(mobLocation.toVector()).normalize();

        // Make the spider jump towards the target
        Vector leap = direction.multiply(leapStrength).setY(0.4); // Ensure vertical component is included for the jump
        mob.getBukkitEntity().setVelocity(leap);

        // Optionally: add visual or sound effects for the leap
        mob.getBukkitEntity().getWorld().playSound(mob.getBukkitEntity().getLocation(), Sound.ENTITY_SPIDER_STEP, 1.0f, 1.0f);
    }

    public double getLeapStrength() {
        return leapStrength;
    }
}