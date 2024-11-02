package org.enissay.dungeonssim.entities.hostile.undeads;

import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class UndeadKnight extends Undead{

    public UndeadKnight(Location loc, int mobLevel, double healthMultiplier) {
        super(loc, mobLevel, healthMultiplier);
        final ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
        final ItemStack leg = new ItemStack(Material.IRON_LEGGINGS);
        final ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        final ItemStack weapon = new ItemStack(Material.IRON_SWORD);
        final ItemStack shield = new ItemStack(Material.SHIELD);

        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);
        equipItem(EquipmentSlot.MAINHAND, weapon);
        equipItem(EquipmentSlot.OFFHAND, shield);
    }

    @Override
    public String getEntityCustomName() {
        return "Undead Knight";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{"aef904c66f4cd357eb80a1e405783f521d284503435aede603c89db15e685709"};
    }
}
