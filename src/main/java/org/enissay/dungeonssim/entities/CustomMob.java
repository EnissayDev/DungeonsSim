package org.enissay.dungeonssim.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface CustomMob {

    String getEntityCustomName();
    Set<ItemStack> getDrops();
    Entity getNMSEntity();
    default boolean isBoss() {
        return false;
    }

    default String[] getCustomHeads(){
        return null;
    }
}
