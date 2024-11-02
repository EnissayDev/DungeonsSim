package org.enissay.dungeonssim.entities.abilities.impl;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;
import org.enissay.dungeonssim.entities.hostile.skeleton.CustomArrow;

public class DarkBoltAbility extends Ability {

    public DarkBoltAbility(long cooldown, double range) {
        super(cooldown, range);
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        if (target == null) {
            return;
        }

        Vec3 direction = target.position().subtract(mob.position()).normalize();
        Location location = mob.getBukkitEntity().getLocation();
        double attackDamage = mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        CustomArrow arrow = new CustomArrow((Level) mob.level, location.getX(), location.getY() + mob.getEyeHeight(), location.getZ(), (int) attackDamage);
        arrow.setOwner(mob);
        arrow.shoot(direction.x(), direction.y(), direction.z(), 2.0F, 1.0F); // Shoot with 2.0 velocity and 1.0 accuracy
        mob.level.addFreshEntity(arrow);

        //arrow.setBaseDamage(8.0); // Set damage to 8.0
        mob.swing(InteractionHand.MAIN_HAND);
        mob.swing(InteractionHand.OFF_HAND);
    }
}