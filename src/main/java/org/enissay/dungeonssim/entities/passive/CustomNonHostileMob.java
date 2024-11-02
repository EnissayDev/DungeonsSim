package org.enissay.dungeonssim.entities.passive;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.AbstractCustomMob;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomNonHostileMob extends AbstractCustomMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "CustomNonHostileMob");

    public CustomNonHostileMob(Location loc, int mobLevel, double healthMultiplier) {
        super(EntityType.ZOMBIE, loc, KEY, ChatColor.GREEN, 0.1, mobLevel, healthMultiplier);
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    /*@Override
    public AttributeMap getAttributes() {
        return new AttributeMap(Cow.createAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .build());
    }*/

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.COW_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.COW_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.COW_DEATH;
    }

    @Override
    public String getEntityCustomName() {
        return "Softie";
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> items = new HashSet<>();
        items.add(new ItemStack(Material.LEATHER, 2));
        return items;
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        attrs.put(Attributes.MAX_HEALTH, 100D);
        return attrs;
    }
}