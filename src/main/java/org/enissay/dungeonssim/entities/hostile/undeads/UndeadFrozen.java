package org.enissay.dungeonssim.entities.hostile.undeads;

import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class UndeadFrozen extends Undead{

    public UndeadFrozen(Location loc, int mobLevel, double healthMultiplier) {
        super(loc, mobLevel, healthMultiplier);
        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        final LeatherArmorMeta meta = (LeatherArmorMeta) chest.getItemMeta();
        meta.setColor(Color.AQUA);
        chest.setItemMeta(meta);
        final ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        final LeatherArmorMeta meta2 = (LeatherArmorMeta) leg.getItemMeta();
        meta2.setColor(Color.AQUA);
        leg.setItemMeta(meta2);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        final LeatherArmorMeta meta3 = (LeatherArmorMeta) boots.getItemMeta();
        meta3.setColor(Color.AQUA);
        boots.setItemMeta(meta3);

        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);

        //addAbility(new FireballAbility(5000, 10)); // 5-second cooldown, 10-block range
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public String getEntityCustomName() {
        return "Frozen Undead";
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{"4509fbb604ef177ebe0f4f067ce6dacc06dc943b3597e92757e891b513dde63d"};
    }
}
