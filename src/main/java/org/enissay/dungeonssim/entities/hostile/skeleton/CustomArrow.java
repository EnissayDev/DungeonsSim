package org.enissay.dungeonssim.entities.hostile.skeleton;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.entities.AbstractCustomMob;

public class CustomArrow extends Arrow {

    private long spawnTime;
    private int customDamage;

    public CustomArrow(EntityType<? extends Arrow> entityType, Level level, int customDamage) {
        super(entityType, level);
        this.setBaseDamage(customDamage);
        this.spawnTime = System.currentTimeMillis();
        this.customDamage = customDamage;
    }

    public CustomArrow(Level level, double x, double y, double z, int customDamage) {
        super(EntityType.ARROW, level);
        this.setPos(x, y, z);
        this.setBaseDamage(customDamage);
        this.spawnTime = System.currentTimeMillis();
        this.customDamage = customDamage;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof AbstractCustomMob)
            // Pass through the AbstractCustomMob
            return;

        super.onHitEntity(result);
    }

    @Override
    public void tick() {
        super.tick();
        // Remove arrow after 5 seconds if it's in a block
        if (this.inGround && System.currentTimeMillis() - this.spawnTime > 5000) {
            this.discard();
        }
    }

}