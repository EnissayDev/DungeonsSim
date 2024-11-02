package org.enissay.dungeonssim.entities.hostile.spiders;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.abilities.impl.SpiderLeapAbility;
import org.enissay.dungeonssim.entities.hostile.AbstractHostileCustomMob;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tarantula extends AbstractHostileCustomMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "CustomSpider");

    public Tarantula(Location loc, int mobLevel, double healthMultiplier) {
        super(EntityType.SPIDER, loc, KEY, mobLevel, healthMultiplier);

        this.addAbility(new SpiderLeapAbility(10000, 10.0, 1.5));
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        attrs.put(Attributes.ATTACK_DAMAGE, 6.0);
        attrs.put(Attributes.FOLLOW_RANGE, 20.0);
        attrs.put(Attributes.MOVEMENT_SPEED, .4);
        return attrs;
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public String getEntityCustomName() {
        return "Tarantula";
    }

    @Override
    public Set<ItemStack> getDrops() {
        return null;
    }
}
