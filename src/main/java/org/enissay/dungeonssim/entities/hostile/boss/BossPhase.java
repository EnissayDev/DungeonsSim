package org.enissay.dungeonssim.entities.hostile.boss;

import net.minecraft.world.entity.LivingEntity;
import org.enissay.dungeonssim.entities.abilities.Ability;

import java.util.List;

public abstract class BossPhase {

    private double threshold;

    public BossPhase(double threshold) {
        this.threshold = threshold;
    }

    public void startPhase(AbstractBossMob boss) {
        onStartPhase(boss);
    }

    public double getThresholdHealth(double maxHealth) {
        return maxHealth * threshold;
    }

    protected abstract void onStartPhase(AbstractBossMob boss);
}