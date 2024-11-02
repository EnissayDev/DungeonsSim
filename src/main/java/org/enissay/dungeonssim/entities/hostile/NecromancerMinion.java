package org.enissay.dungeonssim.entities.hostile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.hostile.undeads.Undead;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NecromancerMinion extends Undead {
    public NecromancerMinion(Location loc, int mobLevel, double healthMultiplier) {
        super(loc, mobLevel, healthMultiplier);
        final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        final ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        equipItem(EquipmentSlot.HEAD, helmet);
        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);

        setBaby(true);
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> drops = new HashSet<>();
        return drops;
    }

    @Override
    public String getEntityCustomName() {
        return "Necromancer Minion";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{};
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        attrs.put(Attributes.ATTACK_DAMAGE, 5.0);
        attrs.put(Attributes.FOLLOW_RANGE, 20.0);
        attrs.put(Attributes.MAX_HEALTH, 20.0);
        attrs.put(Attributes.MOVEMENT_SPEED, 0.3);
        return attrs;
    }
}