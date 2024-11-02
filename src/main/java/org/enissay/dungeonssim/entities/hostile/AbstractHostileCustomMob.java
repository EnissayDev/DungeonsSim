package org.enissay.dungeonssim.entities.hostile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.enissay.dungeonssim.entities.AbstractCustomMob;

public abstract class AbstractHostileCustomMob extends AbstractCustomMob {

    private Goal meleeGoal;

    protected AbstractHostileCustomMob(EntityType<? extends PathfinderMob> entityType, Location spawnLocation, NamespacedKey key, int mobLevel, double healthMultiplier) {
        super(entityType, spawnLocation, key, ChatColor.RED, .05, mobLevel, healthMultiplier);

        Goal meleeGoal = new MeleeAttackGoal(this, 1.0, true);
        this.meleeGoal = meleeGoal;
        this.goalSelector.addGoal(1, meleeGoal);
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public Goal getMeleeGoal() {
        return meleeGoal;
    }
}