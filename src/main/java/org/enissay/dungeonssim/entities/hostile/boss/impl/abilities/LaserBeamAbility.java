package org.enissay.dungeonssim.entities.hostile.boss.impl.abilities;

import com.google.common.base.Preconditions;
import fr.skytasul.guardianbeam.Laser;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.EventManager;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.abilities.Ability;
import org.enissay.dungeonssim.entities.hostile.boss.AbstractBossMob;
import org.enissay.dungeonssim.utils.FormatUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LaserBeamAbility extends Ability implements Listener {
    private int duration;
    private Location[] selection;
    private ArmorStand orb;
    private Map<ArmorStand, Double> map;

    public LaserBeamAbility(long cooldown, double range, int duration, Location[] selection) {
        super(cooldown, range);
        this.duration = duration;
        this.selection = selection;
        this.map = new HashMap<>();
        EventManager.on(EntityDamageEvent.class, event -> {
            if (event.getEntity().getType() == EntityType.ARMOR_STAND &&
            event.getEntity().getUniqueId().equals(orb.getUniqueId())) {
                final double damage = event.getDamage();
                final boolean isDead = addDamage((ArmorStand) event.getEntity(), damage);

                if (!isDead)
                    event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 2f);
                else {
                    event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 2f, .1f);
                    event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
                    event.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, event.getEntity().getLocation(), 10, 1.0, 1.0, 1.0, 0.1);
                }
                event.setCancelled(true);
            }
        });
    }

    public boolean addDamage(final ArmorStand armorStand, final double damage) {
        if (map.get(armorStand) == null || !map.containsKey(armorStand)) map.put(armorStand, damage);
        else
            map.put(armorStand, map.get(armorStand) + damage);
        if (map.get(armorStand) >= armorStand.getMaxHealth()) {
            armorStand.remove();
            return true;
        }
        return false;
    }

    @Override
    protected void execute(AbstractCustomMob mob, LivingEntity target) {
        try {
            Player player = getPlayerFromLivingEntity(target);
            Laser guardianLaser = new Laser.GuardianLaser(mob.getBukkitEntity().getLocation().add(0, 1, 0), player,
                    duration, 40).durationInTicks();
            guardianLaser.start(DungeonsSim.getInstance());

            ArmorStand counter = null;
            Location loc = null;
            if (selection != null) {
                final Location randomLoc = getRandomLocation(selection[0], selection[1]).subtract(0, .4, 0);
                this.orb = (ArmorStand) player.getWorld().spawnEntity(randomLoc, EntityType.ARMOR_STAND);
                this.orb.setMaxHealth(mob.getMaxHealth()/4);
                this.orb.setHealth(this.orb.getMaxHealth());
                this.orb.setVisible(false);
                this.orb.setGravity(false);
                this.orb.setHelmet(NBTEditor.getHead("http://textures.minecraft.net/texture/74332cd835d05bfca3f0ef445d82ca212cd910249cc7371653ab7b2970d1c0f3"));
                this.orb.setCustomNameVisible(true);
                this.orb.setArms(false);
                this.orb.setCanPickupItems(false);

                counter = (ArmorStand) player.getWorld().spawnEntity(randomLoc.clone().add(0, .4, 0), EntityType.ARMOR_STAND);
                counter.setVisible(false);
                counter.setGravity(false);
                counter.setInvulnerable(true);
                counter.setCustomNameVisible(true);
                counter.setArms(false);

                loc = randomLoc.clone();
            }
            ArmorStand finalCounter = counter;
            Location initialLoc = loc.clone();
            Location finalLoc = loc.clone().add(0, .1, 0);
            new BukkitRunnable() {
                int time;
                @Override
                public void run() {
                    if ((orb == null || orb.isDead()) && time < duration) {
                        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 32) { // Make bigger for more particles, make smaller for less. Number of particles is the number * 2
                            double x = Math.cos(angle) * 5;
                            double z = Math.sin(angle) * 5;
                            finalLoc.add(x, 0, z);
                            finalLoc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, finalLoc, 1);
                            finalLoc.subtract(x, 0, z);
                        }
                        /*guardianLaser.stop();
                        if (finalCounter != null && !finalCounter.isDead())
                            finalCounter.remove();
                        cancel();*/
                    }
                    if (mob.isBoss() && mob instanceof AbstractBossMob abm) {
                        if (abm.getCurrentPhaseIndex() != 0) {
                            if (!orb.isDead()) orb.remove();
                            if (finalCounter != null && !finalCounter.isDead())
                                finalCounter.remove();
                            cancel();
                        }
                    }
                    if (mob == null || !mob.isAlive() || time >= duration){
                        if (!(orb.isDead() && isOnCircle(player, initialLoc, 5))) {
                            player.damage(mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 1.4);
                            orb.remove();
                        }
                        if (finalCounter != null && !finalCounter.isDead())
                            finalCounter.remove();
                        cancel();
                    }
                    try {
                        if (finalCounter != null && !finalCounter.isDead()) {
                            final int currentTimer = ((duration - time)/20) + 1;
                            if (time % 20  == 0) {
                                finalCounter.getWorld().playSound(finalCounter.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                            }
                            String result = ChatColor.RED + ChatColor.BOLD.toString() + currentTimer;
                            if (orb.isDead() || orb == null) result+= ChatColor.DARK_GRAY + " - " + ChatColor.LIGHT_PURPLE + "STAY IN THE CIRCLE";
                            finalCounter.setCustomName(result);
                        }
                        final double healthRemaining = orb.getMaxHealth() - (map.get(orb) != null ? map.get(orb) : 0);
                        //                        beaconName.setText(ChatColor.DARK_GREEN + "[Lvl. " + LichKingMortis.this.getMobLevel() + "] " + LichKingMortis.this.getColor() + "Heart of Regeneration" + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(blockHeart.get(beacon)) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(maxBeaconHealth) + "❤");
                        orb.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + mob.getMobLevel() + "] " + ChatColor.AQUA + "Shield Orb" + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(healthRemaining) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(orb.getMaxHealth()) + "❤");
                        guardianLaser.moveStart(mob.getBukkitEntity().getLocation().clone().add(0, 1, 0));
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                    time++;
                }
            }.runTaskTimer(DungeonsSim.getInstance(), 0, 0);
            /*new BukkitRunnable() {
                @Override
                public void run() {
                    getPlayerFromLivingEntity(target).damage(mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                }
            }.runTaskLater(DungeonsSim.getInstance(), duration);*/
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

    }
    public final boolean isOnCircle(Player player, Location location, double radius) {
        if (player != null && location != null) {
            final Location locationPlayer = player.getLocation();

            return locationPlayer.distanceSquared(location) < radius;
        } else {
            return false;
        }
    }

    public Location getRandomLocation(Location loc1, Location loc2) {
        Preconditions.checkArgument(loc1.getWorld() == loc2.getWorld());
        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());

        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        return new Location(loc1.getWorld(), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
    }

    public double randomDouble(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(Math.abs(max - min + 1));
    }

    public Player getPlayerFromLivingEntity(LivingEntity nmsEntity) {
        // Get the Bukkit entity from the NMS entity
        org.bukkit.entity.Entity bukkitEntity = nmsEntity.getBukkitEntity();

        // Check if the entity is a player
        if (bukkitEntity instanceof Player) {
            return (Player) bukkitEntity;
        }

        // Return null if the entity is not a player
        return null;
    }
}
