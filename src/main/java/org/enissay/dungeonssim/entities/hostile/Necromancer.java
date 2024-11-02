package org.enissay.dungeonssim.entities.hostile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.abilities.impl.CurseOfWeaknessAbility;
import org.enissay.dungeonssim.entities.abilities.impl.DarkBoltAbility;
import org.enissay.dungeonssim.entities.hostile.skeleton.CustomSkeleton;

import javax.annotation.Nullable;
import java.util.*;

public class Necromancer extends CustomSkeleton {

    private List<AbstractHostileCustomMob> minions;

    private boolean hasBeenUsed;//, immuneToDamage;
    private Location home;

    public Necromancer(Location loc, int mobLevel, double healthMultiplier) {
        super(loc, mobLevel, healthMultiplier, 30);
        this.home = loc;
        this.minions = new ArrayList<>();
        this.hasBeenUsed = false;
        //this.immuneToDamage = false;

        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        final LeatherArmorMeta meta = (LeatherArmorMeta) chest.getItemMeta();
        meta.setColor(Color.BLUE);
        chest.setItemMeta(meta);
        final ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        final LeatherArmorMeta meta2 = (LeatherArmorMeta) leg.getItemMeta();
        meta2.setColor(Color.BLUE);
        leg.setItemMeta(meta2);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        final LeatherArmorMeta meta3 = (LeatherArmorMeta) boots.getItemMeta();
        meta3.setColor(Color.BLUE);
        boots.setItemMeta(meta3);

        final ItemStack hand = new ItemStack(Material.ENCHANTED_BOOK);

        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);
        equipItem(EquipmentSlot.MAINHAND, hand);

        applyCustomHead();

        this.setUseRandomAbilities(true);
        this.getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(this.level.dimension(),
                new BlockPos(home.getBlockX(), home.getBlockY(), home.getBlockZ())));

        this.addAbility(new DarkBoltAbility(20000, 15.0)); // Dark Bolt every 20 seconds
        this.addAbility(new CurseOfWeaknessAbility(100 * 1000, 10.0));

    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public String getEntityCustomName() {
        return "Necromancer";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.BLUE;
    }

    @Override
    public Set<ItemStack> getDrops() {
        Set<ItemStack> items = new HashSet<>();
        //items.add(new ItemStack(Material.COAL_BLOCK, 5));
        return items;
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{"54e5a2321e639fdc9d42434aff3d7c674b4a88b2e45ed9f03723befecc9a3e7c"};
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        attrs.put(Attributes.ATTACK_DAMAGE, 12.0);
        attrs.put(Attributes.FOLLOW_RANGE, 35.0);
        //attrs.put(Attributes.MAX_HEALTH, 100D);
        attrs.put(Attributes.MOVEMENT_SPEED, .2);
        return attrs;
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float v) {

    }

    /*@Override
    public boolean hurt(DamageSource source, float amount) {
        if (immuneToDamage) {
            return false; // Immune to damage during phase transitions
        }
        return super.hurt(source, amount);
    }*/

    @Override
    public void tick() {
        super.tick();

        if (!hasBeenUsed && this.getHealth() <= this.getMaxHealth() * 0.25 && this.isAlive()) {
            Location summonLocation = this.getBukkitEntity().getLocation();

            this.swing(InteractionHand.MAIN_HAND);
            this.swing(InteractionHand.OFF_HAND);

            summonLocation.getWorld().spawnParticle(Particle.SPELL, summonLocation.clone().add(0, 1.5, 0), 30, 0.5, 0.5, 0.5, 0);
            summonLocation.getWorld().playSound(summonLocation, Sound.ENTITY_WITHER_SPAWN, 1, 2);
            setImmune(true);
            final int minionCount = 4;

            for (int i = 0; i < minionCount; i++) {

                Location spawnLoc = summonLocation.clone().add((Math.random() - 0.5) * 2, 0, (Math.random() - 0.5) * 2);
                Block groundBlock = getLowestBlockBeneath(spawnLoc);  // Get the block type at that location
                Material blockMaterial = groundBlock.getType();  // Get the material of the block
                AbstractHostileCustomMob minion = new NecromancerMinion(spawnLoc.getBlock().getLocation().subtract(0, 2, 0), this.getMobLevel(), this.getHealthMultiplier());

                minion.setNoGravity(true);
                minion.setNoAi(true);
                minion.setInvulnerable(true);

                registerMinion(minion);

                new BukkitRunnable() {
                    double yOffset = 0;

                    @Override
                    public void run() {
                        if (yOffset >= 2) {
                            minion.setNoGravity(false);
                            minion.setNoAi(false);
                            minion.setInvulnerable(false);
                            this.cancel();
                        }

                        yOffset += 0.05;
                        Location currentLocation = minion.getBukkitEntity().getLocation();
                        summonLocation.getWorld().spawnParticle(Particle.BLOCK_DUST, groundBlock.getLocation().clone().add(0, 1, 0), 20, blockMaterial.createBlockData());
                        summonLocation.getWorld().playSound(groundBlock.getLocation().clone().add(0, 1, 0), Sound.BLOCK_NETHER_BRICKS_BREAK, 1, .1f);
                        //summonLocation.getWorld().spawnParticle(Particle.SPELL_WITCH, spawnLoc, 30, 0.5, 0.5, 0.5, 0);
                        minion.getBukkitEntity().teleport(currentLocation.add(0, 0.05, 0));
                    }
                }.runTaskTimer(DungeonsSim.getInstance(), 0, 1); // Elevate minion over time
                new BukkitRunnable(){

                    @Override
                    public void run() {
                        setImmune(false);
                    }
                }.runTaskLater(DungeonsSim.getInstance(), 20 * 5);
                //AbstractHostileCustomMob minion = new NecromancerMinion(spawnLoc, this.getMobLevel(), this.getHealthMultiplier());
                //summonLocation.getWorld().spawnParticle(Particle.SPELL_WITCH, spawnLoc, 30, 0.5, 0.5, 0.5, 0);
                //registerMinion(minion);
            }
            hasBeenUsed = true;
        }
    }

    public static Block getLowestBlockBeneath(Location location) {
        World world = location.getWorld();

        if (world == null) {
            throw new IllegalArgumentException("The entity's world cannot be null");
        }

        // Start from the entity's position and move downward
        for (int y = location.getBlockY() - 1; y >= 0; y--) {
            Block block = world.getBlockAt(location.getBlockX(), y, location.getBlockZ());
            if (block.getType() != Material.AIR) {
                return block;
            }
        }

        // If no non-air block found, return the bottom of the world
        return world.getBlockAt(location.getBlockX(), 0, location.getBlockZ());
    }

    public void registerMinion(AbstractHostileCustomMob minion) {
        minions.add(minion);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        for (AbstractHostileCustomMob minion : minions) {
            minion.getBukkitEntity().remove();
        }
    }
}