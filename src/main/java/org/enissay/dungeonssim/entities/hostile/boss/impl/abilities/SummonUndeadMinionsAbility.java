package org.enissay.dungeonssim.entities.hostile.boss.impl.abilities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;
import org.enissay.dungeonssim.entities.hostile.AbstractHostileCustomMob;
import org.enissay.dungeonssim.entities.hostile.NecromancerMinion;
import org.enissay.dungeonssim.entities.hostile.undeads.Undead;
import org.enissay.dungeonssim.entities.hostile.undeads.UndeadWarrior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummonUndeadMinionsAbility extends Ability {

    private int minionCount;
    private List<AbstractHostileCustomMob> minions;

    public SummonUndeadMinionsAbility(long cooldown, double range, int minionCount) {
        super(cooldown, range);
        this.minionCount = minionCount;
        this.minions = new ArrayList<>();
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        Location summonLocation = mob.getBukkitEntity().getLocation();

        summonLocation.getWorld().spawnParticle(Particle.SPELL, summonLocation.clone().add(0, 1.5, 0), 30, 0.5, 0.5, 0.5, 0);
        summonLocation.getWorld().playSound(summonLocation, Sound.ENTITY_WITHER_SPAWN, 1, 2);
        for (int i = 0; i < minionCount; i++) {

            Location spawnLoc = summonLocation.clone().add((Math.random() - 0.5) * 2, 0, (Math.random() - 0.5) * 2);
            Block groundBlock = getLowestBlockBeneath(spawnLoc);  // Get the block type at that location
            Material blockMaterial = groundBlock.getType();  // Get the material of the block
            AbstractHostileCustomMob minion = new Undead(spawnLoc.getBlock().getLocation().subtract(0, 2, 0), (mob.getMobLevel() - 10) > 0 ? (mob.getMobLevel() - 10) : 1, mob.getHealthMultiplier());

            minion.setNoGravity(true);
            minion.setNoAi(true);
            minion.setInvulnerable(true);

            createMultipleCircles(groundBlock.getLocation().clone(), 1, 4);

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
        }
    }


    public void registerMinion(AbstractHostileCustomMob minion) {
        minions.add(minion);
    }

    private ArrayList<Location> getCircleBlocks(Location loc, double radius, double height, boolean hollow, boolean sphere)
    {
        ArrayList<Location> circleblocks = new ArrayList<Location>();
        double cx = loc.getBlockX();
        double cy = loc.getBlockY();
        double cz = loc.getBlockZ();

        for (double y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height + 1); y++)
        {
            for (double x = cx - radius; x <= cx + radius; x++)
            {
                for (double z = cz - radius; z <= cz + radius; z++)
                {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);

                    if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1)))
                    {
                        Location l = new Location(loc.getWorld(), x, y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    private void revertBlocks(Map<Block, BlockData> originalBlocks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Block, BlockData> entry : originalBlocks.entrySet()) {
                    entry.getKey().setBlockData(entry.getValue());
                }
            }
        }.runTaskLater(DungeonsSim.getInstance(), 20); // Adjust the delay for how long the blocks stay as obsidian
    }

    private Map<Block, BlockData> createMultipleCircles(Location center, double minRange, double maxRange) {
        World world = center.getWorld();
        Map<Block, BlockData> originalBlocks = new HashMap<>();

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (step >= maxRange) {
                    cancel();
                    revertBlocks(originalBlocks);
                    return;
                }

                double range = minRange + step;
                getCircleBlocks(center.clone().add(0, -1, 0), range, 1, true, false).forEach(locs -> {
                    Block block = world.getBlockAt(locs);
                    originalBlocks.put(block, block.getBlockData());
                    Material mat;
                    if (range % 2 == 0) mat = Material.NETHER_BRICKS;
                    else mat = Material.RED_NETHER_BRICKS;
                    block.setType(mat);
                });

                center.clone().add(0, -1, 0).getWorld().playSound(center.clone().add(0, -1, 0), Sound.BLOCK_NETHER_BRICKS_BREAK, 1, .1f);

                step++;
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, 5); // Adjust the delay between steps as needed
        return originalBlocks;
    }

    public Block getLowestBlockBeneath(Location location) {
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

    public List<AbstractHostileCustomMob> getMinions() {
        return minions;
    }

    private double randomOffset() {
        return (Math.random() - 0.5) * 5.0;
    }
}