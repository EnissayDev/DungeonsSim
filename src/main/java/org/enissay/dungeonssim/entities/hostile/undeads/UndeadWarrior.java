package org.enissay.dungeonssim.entities.hostile.undeads;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.entities.abilities.impl.ShakeEarthAbility;

public class UndeadWarrior extends Undead{

    public UndeadWarrior(Location loc, int mobLevel, double healthMultiplier) {
        super(loc, mobLevel + 4, healthMultiplier);
        final ItemStack chest = new ItemStack(Material.GOLDEN_CHESTPLATE);
        final ItemStack leg = new ItemStack(Material.GOLDEN_LEGGINGS);
        final ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);
        final ItemStack weapon = new ItemStack(Material.GOLDEN_SWORD);

        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);
        equipItem(EquipmentSlot.MAINHAND, weapon);

        addAbility(new ShakeEarthAbility(20 * 1000, 5D));
    }

    @Override
    public void awardKillScore(Entity entity, int i, DamageSource damagesource) {
        //Bukkit.broadcastMessage(entity.getBukkitEntity().getName());
        super.awardKillScore(entity, i, damagesource);
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{"5c169fcb0e9ced5ba3f0807bf7f8478f0226e919395382bcece7ffbc7239f5f5"};
    }

    @Override
    public String getEntityCustomName() {
        return "Undead Warrior";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_PURPLE;
    }

}
