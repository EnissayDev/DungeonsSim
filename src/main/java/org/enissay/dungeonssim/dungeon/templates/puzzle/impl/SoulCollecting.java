package org.enissay.dungeonssim.dungeon.templates.puzzle.impl;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.EventManager;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.templates.puzzle.IPuzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleType;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.entities.hostile.Necromancer;
import org.enissay.dungeonssim.handlers.EntitiesHandler;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SoulCollecting implements IPuzzle {
    List<AbstractCustomMob> entities = new ArrayList<>();

    @Override
    public PuzzleType getType() {
        return PuzzleType.SOUL_COLLECTING;
    }

    @Override
    public void initEvents(DungeonRoom room) {
        final AtomicInteger killed = new AtomicInteger();
        final Location hologramLoc = room.getLocationFromTemplate("holo");

        if (hologramLoc != null) {
            final Hologram holo = DHAPI.createHologram("holo_2_dungeon_" + room.getDungeon().getID(), hologramLoc, false);
            DHAPI.addHologramLine(holo, "&d&lSOUL COLLECTING");
            DHAPI.addHologramLine(holo, "");
            DHAPI.addHologramLine(holo, "&eYou will have to kills the &3Necromancers");
            DHAPI.addHologramLine(holo, "&earound you to complete the puzzle!");
            DHAPI.addHologramLine(holo, "&eThis Soul Meter will help you track your progress.");
            DHAPI.addHologramLine(holo, "");
            DHAPI.addHologramLine(holo, "");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (holo != null)
                        DHAPI.setHologramLine(holo, 6, "&aPROGRESS: &b" + killed.get() + "&7/&3" + entities.size());
                    if (room.getPuzzle() != null && room.getPuzzle().isCompleted()) {
                        if (holo != null)
                            DHAPI.setHologramLine(holo, 6, "&aPROGRESS: &2COMPLETED");
                        cancel();
                    }
                }
            }.runTaskTimer(DungeonsSim.getInstance(), 0, 20);

            room.getDungeon().addHologram(holo);
        }

        for (int i = 1; i <= 8; i++) {
            Location spawnLocation = room.getLocationFromTemplate("mobSpawn_" + i);
            if (spawnLocation != null) {
                AbstractCustomMob entity = new Necromancer(spawnLocation.add(0, 1, 0), room.getDungeon().getDungeonDifficulty().getLevel(), (room.getDungeon().getPlayers().size() - 1));
                entities.add(entity);
                room.getDungeon().addCustomMob(entity.getNMSEntity());
            }
        }
        //"container_center":
        //"holo":

        EventManager.on(EntityDeathEvent.class, event -> {
            Entity rawEntity = event.getEntity();
            if (!(rawEntity instanceof Player)) {
                LivingEntity entity = (LivingEntity) rawEntity;

                if (entity instanceof CraftCreature) {
                    CraftCreature craftCreature = (CraftCreature) entity;
                    net.minecraft.world.entity.Entity nmsEntity = craftCreature.getHandle();

                    if (nmsEntity instanceof AbstractCustomMob acm) {
                        if (entities.contains(acm)) {
                            Location centerLocation = room.getLocationFromTemplate("container_center");
                            if (centerLocation != null) {
                                Location clone = centerLocation.clone().add(0, (killed.get() * 3), 0);
                                int maxY = 92;
                                if (clone.getBlockY() <= maxY) {
                                    //for (int i = 1; i <= 3; i++)
                                    getCircleBlocks(clone, 5, 3, false, false).forEach(locs -> locs.getBlock().setType(Material.CYAN_CONCRETE));
                                    //cylinder(clone.clone().add(0, i, 0), Material.WATER, 4);
                                }
                            }
                            killed.getAndIncrement();
                            if (event.getEntity().getKiller() != null) {
                                event.getEntity().getKiller().sendTitle("", (ChatColor.AQUA.toString() + killed.get() + ChatColor.GRAY + "/" + ChatColor.BLUE + entities.size() + ChatColor.WHITE + " Souls collected"), 0, 30, 10);

                                if (killed.get() >= entities.size()) {
                                    final Puzzle puzzle = room.getPuzzle();
                                    if (puzzle != null)
                                        puzzle.complete(event.getEntity().getKiller());
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private static ArrayList<Location> getCircleBlocks(Location loc, double radius, double height, boolean hollow, boolean sphere)
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

    @Override
    public void onCompletion(DungeonRoom room) {
        entities.clear();
    }
}
