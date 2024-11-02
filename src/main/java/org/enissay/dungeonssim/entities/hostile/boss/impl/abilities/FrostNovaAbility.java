package org.enissay.dungeonssim.entities.hostile.boss.impl.abilities;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;

import java.util.List;

public class FrostNovaAbility extends Ability {

    public FrostNovaAbility(long cooldown, double range) {
        super(cooldown, range);
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        World world = mob.getBukkitEntity().getLocation().getWorld();
        List<Player> players = world.getPlayers();

        for (Player player : players) {
            if (player.getLocation().distance(mob.getBukkitEntity().getLocation()) <= getRange()) {
                player.damage(5.0, mob.getBukkitEntity());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
            }
        }

        Bukkit.broadcastMessage("Lich King Mortis casts Frost Nova!");
    }
}