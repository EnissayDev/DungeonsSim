package org.enissay.dungeonssim.entities.hostile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.DungeonsSim;
import org.w3c.dom.Attr;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomHostileMob extends AbstractHostileCustomMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "CustomHostileMob");

    public CustomHostileMob(Location loc, int mobLevel, double healthMultiplier) {
        super(EntityType.ENDERMAN, loc, KEY, mobLevel, healthMultiplier);
    }

    //removed this method since it executes every tick so you can't modify it
    /*@Override
    public AttributeMap getAttributes() {
        return new AttributeMap(Zombie.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.1)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.MOVEMENT_SPEED, .4)
                .build());
    }*/

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    public String getEntityCustomName() {
        return "Nigga";
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> items = new HashSet<>();
        items.add(new ItemStack(Material.COAL_BLOCK, 5));
        return items;
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        attrs.put(Attributes.ATTACK_DAMAGE, 0.1);
        attrs.put(Attributes.FOLLOW_RANGE, 35.0);
        attrs.put(Attributes.MAX_HEALTH, 100D);
        attrs.put(Attributes.MOVEMENT_SPEED, .4);
        return attrs;
    }
}