package org.enissay.dungeonssim.entities.hostile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BossTest extends AbstractHostileCustomMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "CustomMiniBoss");
    private boolean inPhaseTwo = false;
    private final UUID bossUUID;
    private boolean immuneToDamage = false;

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        return null;
    }

    // Other fields and constants omitted for brevity
    private enum BossPhase {
        PHASE_ONE,
        PHASE_TWO
    }

    private BossPhase phase = BossPhase.PHASE_ONE;

    // Define your goals as fields
    private MeleeAttackGoal meleeAttackGoal;
    private LookAtPlayerGoal lookAtPlayerGoal;
    private RandomLookAroundGoal randomLookAroundGoal;
    private NearestAttackableTargetGoal<Player> nearestAttackableTargetGoal;

    public BossTest(Location loc) {
        super(EntityType.ZOMBIE, loc, KEY, 1, 1);
        this.bossUUID = this.getUUID();

        // Initialize your goals
        this.meleeAttackGoal = new MeleeAttackGoal(this, 1.0, true);
        this.lookAtPlayerGoal = new LookAtPlayerGoal(this, Player.class, 8.0F);
        this.randomLookAroundGoal = new RandomLookAroundGoal(this);
        this.nearestAttackableTargetGoal = new NearestAttackableTargetGoal<>(this, Player.class, true);

        // Add initial goals for phase one
        this.goalSelector.addGoal(1, meleeAttackGoal);
        this.goalSelector.addGoal(2, lookAtPlayerGoal);
        this.goalSelector.addGoal(3, randomLookAroundGoal);

        this.targetSelector.addGoal(1, nearestAttackableTargetGoal);
    }

    @Override
    public void tick() {
        super.tick();

        // Check for phase transition
        if (phase == BossPhase.PHASE_ONE && getHealth() <= getMaxHealth() / 2) {
            enterPhaseTwo();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (immuneToDamage) {
            return false; // Immune to damage during phase transitions
        }
        return super.hurt(source, amount);
    }

    private void enterPhaseTwo() {
        phase = BossPhase.PHASE_TWO;

        immuneToDamage = true;

        // Broadcast preparation message
        Bukkit.broadcastMessage("§cThe mini-boss is preparing for phase two!");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isAlive() || getUUID() != bossUUID) cancel();

                Bukkit.broadcastMessage("§cThe mini-boss has become enraged!");

                // Disable movement
                getNavigation().stop();

                // Levitate the mini-boss
                AttributeInstance movementSpeed1 = getAttribute(Attributes.MOVEMENT_SPEED);
                if (movementSpeed1 != null) {
                    movementSpeed1.setBaseValue(0);
                }
                addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0)); // Levitate for 5 seconds

                // Increase movement speed or modify attributes
                AttributeInstance movementSpeed = getAttribute(Attributes.MOVEMENT_SPEED);
                if (movementSpeed != null) {
                    movementSpeed.setBaseValue(movementSpeed.getBaseValue() * 1.5);
                }

                immuneToDamage = false;

                // Add or replace goals for phase two
                goalSelector.addGoal(1, meleeAttackGoal);
                goalSelector.addGoal(2, lookAtPlayerGoal);
                goalSelector.addGoal(3, randomLookAroundGoal);

                targetSelector.addGoal(1, nearestAttackableTargetGoal);
            }
        }.runTaskLater(DungeonsSim.getInstance(), 0); // 100 ticks = 5 seconds
    }

    @Override
    public AttributeMap getAttributes() {
        return new AttributeMap(Zombie.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.FOLLOW_RANGE, 50.0)
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, .25)
                .build());
    }
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    public String getEntityCustomName() {
        return "CustomMiniBoss";
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> items = new HashSet<>();
        items.add(new ItemStack(Material.DIAMOND, 3));
        items.add(new ItemStack(Material.GOLDEN_APPLE, 1));
        return items;
    }
}