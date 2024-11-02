package org.enissay.dungeonssim.entities.abilities.impl;

import com.google.common.collect.Lists;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;

import java.util.ArrayList;
import java.util.List;

public class CurseOfWeaknessAbility extends Ability {

    public CurseOfWeaknessAbility(long cooldown, double range) {
        super(cooldown, range);
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        if (target instanceof Player player) {
            try {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0)); // Weakness effect for 10 seconds
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2)); // Slowness effect for 10 seconds
                List<LivingEntity> targets = new ArrayList<>();
                targets.add(target);

                mob.sendDialogue(targets, "&cYou shall get cursed.", Sound.ENTITY_SKELETON_HURT, .2f, .7f);

                mob.swing(InteractionHand.MAIN_HAND);
                mob.swing(InteractionHand.OFF_HAND);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}