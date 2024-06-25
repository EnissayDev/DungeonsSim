package org.enissay.dungeonssim.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers;

public class BlockUtils {

    public static java.awt.Color getColor(Block block) {
        CraftBlock cb = (CraftBlock) block;
        net.minecraft.world.level.block.state.BlockState bs = cb.getNMS();
        net.minecraft.world.level.material.MaterialColor mc = bs.getMapColor(cb.getCraftWorld().getHandle(), cb.getPosition());
        return new java.awt.Color(mc.col);
    }

    public static java.awt.Color getColor(Material material) {
        net.minecraft.world.level.block.Block block = CraftMagicNumbers.getBlock(material);
        net.minecraft.world.level.material.MaterialColor mc = block.defaultMaterialColor();
        return new java.awt.Color(mc.col);
    }
}
