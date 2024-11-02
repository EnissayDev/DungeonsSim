package org.enissay.dungeonssim.entities.abilities.impl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;

import java.util.List;
import java.util.stream.Collectors;

public class ShakeEarthAbility extends Ability {

    public ShakeEarthAbility(long cooldown, double range) {
        super(cooldown, range);
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        double damage = mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue();

        World world = mob.getBukkitEntity().getWorld();
        Location mobLocation = mob.getBukkitEntity().getLocation();
        List<Player> nearbyPlayers = world.getNearbyEntities(mobLocation, getRange(), getRange(), getRange(), entity -> entity instanceof Player)
                .stream().map(entity -> (Player) entity).collect(Collectors.toList());


        // Apply damage and effects to nearby entities
        for (Player entity : nearbyPlayers) {
            entity.damage(damage, mob.getBukkitEntity());
            world.spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 10, 1.0, 1.0, 1.0, 0.1);
        }

        // Visual and auditory effects
        world.playSound(mobLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        world.spawnParticle(Particle.EXPLOSION_LARGE, mobLocation, 10, 1.0, 1.0, 1.0, 0.1);

        // Optionally send dialogue to affected entities
        //sendDialogue(mob, target, "&cThe ground shakes violently!");

        // Add any additional effects like knockback, slowing, etc.
        for (Player entity : nearbyPlayers) {
            Vector direction = entity.getLocation().toVector().subtract(mobLocation.toVector()).normalize().multiply(1.7);
            direction.setY(1.2);
            entity.setVelocity(direction);
        }
    }
}