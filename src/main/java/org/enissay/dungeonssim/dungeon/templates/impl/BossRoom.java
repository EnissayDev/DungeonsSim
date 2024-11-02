package org.enissay.dungeonssim.dungeon.templates.impl;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftCreature;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.enissay.dungeonssim.dungeon.*;
import org.enissay.dungeonssim.dungeon.events.DungeonFinishEvent;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.dungeon.system.DungeonDifficulty;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.system.DungeonType;
import org.enissay.dungeonssim.dungeon.templates.MonsterFrequency;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.hostile.AbstractHostileCustomMob;
import org.enissay.dungeonssim.entities.hostile.boss.impl.LichKingMortis;
import org.enissay.dungeonssim.entities.hostile.undeads.UndeadWarrior;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PartyHandler;
import org.enissay.dungeonssim.profiles.event.PlayerLevelUpEvent;
import org.enissay.dungeonssim.utils.Cuboid;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BossRoom implements DungeonTemplate {

    private Map<Dungeon, AbstractCustomMob> boss;

    @Override
    public String getName() {
        return "BOSS_ROOM";
    }

    @Override
    public DungeonType getType() {
        return DungeonType.HOSTILE;
    }

    @Override
    public int getID() {
        return 3;
    }

    @Override
    public int getMinRooms() {
        return 1;
    }

    @Override
    public int getMaxRooms() {
        return 1;
    }

    @Override
    public void initEvents() {
        boss = new HashMap<>();

        EventManager.on(EntityDeathEvent.class, event -> {
            if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player && !(event.getEntity() instanceof Player)) {
                final Dungeon dungeon = DungeonHandler.getDungeonOf(event.getEntity().getKiller().getUniqueId());
                final DungeonParty dungeonParty = PartyHandler.getPartyOf(event.getEntity().getKiller().getUniqueId());

                if (dungeon != null && event.getEntity() instanceof CraftCreature) {
                    CraftCreature craftCreature = (CraftCreature) event.getEntity();

                    net.minecraft.world.entity.Entity nmsEntity = craftCreature.getHandle();

                    if (nmsEntity instanceof AbstractCustomMob acm) {
                        if (boss.get(dungeon) != null && acm.equals(boss.get(dungeon))) {
                            boss.remove(dungeon);
                            Bukkit.getPluginManager().callEvent(new DungeonFinishEvent(dungeon, dungeonParty, true));
                        }
                    }
                }
            }
            //Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(Bukkit.getPlayer(UUID.fromString(id))));
        });
        EventManager.on(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Location location = event.getFrom();
            final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(location);
            if (dungeonRoom != null &&
                    dungeonRoom.getCuboid().isIn(location) &&
                    dungeonRoom.getDungeon().getPlayers().contains(player.getUniqueId()) &&
                    dungeonRoom.getTemplate().getName().equals(getName())) {
                /*player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + getName() + "-" + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() +
                        ", dungeonID: " + dungeonRoom.getDungeon().getID() +
                        ", players in dungeon: " + dungeonRoom.getDungeon().getPlayers().size() +
                        ", players in current room " + dungeonRoom.getWatchers().size()));*/
                //player.sendMessage("You're in: " + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() + " template: " + dungeonRoom.getTemplate().getName() + " dungeonID: " + dungeonRoom.getDungeon().getID());
                /*List<String> names = new ArrayList<>();
                dungeonRoom.getWatchers().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null)
                        names.add(Bukkit.getPlayer(uuid).getName());
                });*/
                if ((boss.get(dungeonRoom.getDungeon()) == null || !boss.containsKey(dungeonRoom.getDungeon())) &&
                        dungeonRoom.getDungeon().getProgression() >= 50) {
                    final Location loc1 = dungeonRoom.getLocationFromTemplate("floorLocation1");
                    final Location loc2 = dungeonRoom.getLocationFromTemplate("floorLocation2");
                    final Cuboid detection = new Cuboid(loc1, loc2);
                    if (detection.isIn(event.getPlayer())) {
                        MessageUtils.broadcastDungeon(dungeonRoom.getDungeon(), "&a" + player.getName() + " &ehas entered the &c&lBOSS&e's territory.", MessageUtils.BroadcastType.MESSAGE);
                        MessageUtils.broadcastDungeon(dungeonRoom.getDungeon(), "&c&lBOSS &7Awakened", MessageUtils.BroadcastType.SUB_TITLE);
                        MessageUtils.broadcastDungeonSound(dungeonRoom.getDungeon(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, .1f);
                        spawnBoss(dungeonRoom);

                        if (dungeonRoom.getDungeon() != null) dungeonRoom.getDungeon().playSong("dungeon1_bossfight");
                    }
                }
                //player.sendMessage("Players: " + String.join(", ", names));
            }
        });
    }

    public void spawnBoss(final DungeonRoom dungeonRoom) {
        final Location loc = dungeonRoom.getLocationFromTemplate("bossLocation");
        final DungeonDifficulty diff = dungeonRoom.getDungeon().getDungeonDifficulty();
        switch (diff) {
            case EASY:
                LichKingMortis boss = new LichKingMortis(loc.add(0, .1, 0), diff.getLevel() + 9, (1.8 * (dungeonRoom.getDungeon().getPlayers().size() - 1)), dungeonRoom.getDungeon());
                //boss.setDungeon(dungeonRoom.getDungeon());
                dungeonRoom.getDungeon().addCustomMob(boss);
                this.boss.put(dungeonRoom.getDungeon(), boss);
                break;

        }
    }

    @Override
    public MonsterFrequency getMonstersFrequency() {
        return null;
    }
}
