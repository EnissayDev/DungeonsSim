package org.enissay.dungeonssim.entities.hostile.skeleton;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UndeadSkeleton extends CustomSkeleton{

    public UndeadSkeleton(Location loc, int mobLevel, double healthMultiplier) {
        super(loc, mobLevel, healthMultiplier, 23);
        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        final ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        final ItemStack bow = new ItemStack(Material.BOW);

        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);
        equipItem(EquipmentSlot.MAINHAND, bow);

        applyCustomHead();

        //this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 30, 15.0F));
    }

    @Override
    public String getEntityCustomName() {
        return "Undead Archer";
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> items = new HashSet<>();
        //items.add(new ItemStack(Material.COAL_BLOCK, 5));
        return items;
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{"6d7b1d4eabf35350382b465649964a4f5ad81fbc0c9f4149634829db83d69a3"};
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        attrs.put(Attributes.ATTACK_DAMAGE, 12.0);//TO FIX
        attrs.put(Attributes.FOLLOW_RANGE, 35.0);
        //attrs.put(Attributes.MAX_HEALTH, 100D);
        attrs.put(Attributes.MOVEMENT_SPEED, .45);
        return attrs;
    }

}
