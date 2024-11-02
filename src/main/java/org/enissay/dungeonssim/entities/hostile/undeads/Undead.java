package org.enissay.dungeonssim.entities.hostile.undeads;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.hostile.AbstractHostileCustomMob;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Undead extends AbstractHostileCustomMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "Undead");

    public Undead(Location loc, int mobLevel, double healthMultiplier) {
        super(EntityType.ZOMBIE, loc, KEY, mobLevel, healthMultiplier);
        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        final ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);

        applyCustomHead();
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
        return SoundEvents.ZOMBIE_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ZOGLIN_ANGRY;
    }

    @Override
    public String getEntityCustomName() {
        return "Undead";
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> items = new HashSet<>();
        //items.add(new ItemStack(Material.COAL_BLOCK, 5));
        return items;
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{"4896f97c0ed0d4ace72b321136890de3d166520f4824265e497d3eea1261353b",
        "fa6c9b84a8fe0fe383d6068fbc7d030243719f1b10c899adc3658fd34ab35d68"};
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        attrs.put(Attributes.ATTACK_DAMAGE, 2.0);
        attrs.put(Attributes.FOLLOW_RANGE, 35.0);
        //attrs.put(Attributes.ARMOR_TOUGHNESS, 1.0);
        //attrs.put(Attributes.MAX_HEALTH, 100D);
        attrs.put(Attributes.MOVEMENT_SPEED, .3);
        return attrs;
    }
}