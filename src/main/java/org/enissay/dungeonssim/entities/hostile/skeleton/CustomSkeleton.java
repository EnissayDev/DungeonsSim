package org.enissay.dungeonssim.entities.hostile.skeleton;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.hostile.AbstractHostileCustomMob;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class CustomSkeleton extends AbstractHostileCustomMob implements RangedAttackMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "Undead");

    public CustomSkeleton(Location loc, int mobLevel, double healthMultiplier,int fireRate) {
        super(EntityType.SKELETON, loc, KEY, mobLevel, healthMultiplier);

        this.goalSelector.removeGoal(this.getMeleeGoal());
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, fireRate, 15.0F));
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
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    public void performRangedAttack(net.minecraft.world.entity.LivingEntity target, float pullProgress) {
        double attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        Location location = this.getBukkitEntity().getLocation();
        CustomArrow arrow = new CustomArrow((Level) this.level, location.getX(), location.getY() + this.getEyeHeight(), location.getZ(), (int)attackDamage);
        arrow.setOwner(this);
        //arrow.setBaseDamage(attackDamage);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        arrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.level.addFreshEntity(arrow);

        // Make the skeleton swing its arms
        this.swing(InteractionHand.MAIN_HAND);
    }
}
