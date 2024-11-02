package org.enissay.dungeonssim.entities.passive;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
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

public class Dummy extends AbstractCustomMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "Dummy");

    public Dummy(Location loc, int mobLevel, double healthMultiplier) {
        super(EntityType.ZOMBIE, loc.add(0, 1, 0), KEY, ChatColor.GREEN, 0, mobLevel, healthMultiplier);
        //this.setNoGravity(true);
    }

    /*@Override
    public AttributeMap getAttributes() {
        return new AttributeMap(Cow.createAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .build());
    }*/

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Override
    public String getEntityCustomName() {
        return "Dummy";
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> items = new HashSet<>();
        return items;
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        //attrs.put(Attributes.ARMOR, 10000D);
        attrs.put(Attributes.KNOCKBACK_RESISTANCE, 10000D);
        //attrs.put(Attributes.ARMOR_TOUGHNESS, 10000D);
        return attrs;
    }
}
