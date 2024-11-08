package org.enissay.dungeonssim.dungeon.templates.impl;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.enissay.dungeonssim.dungeon.*;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.system.DungeonType;
import org.enissay.dungeonssim.dungeon.templates.MonsterFrequency;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.handlers.DungeonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpawnRoom implements DungeonTemplate {
    @Override
    public String getName() {
        return "SPAWN_ROOM";
    }

    @Override
    public int getID() {
        return 1;
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
        EventManager.on(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Location location = event.getFrom();
            final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(location);
            if (dungeonRoom != null &&
                    dungeonRoom.getCuboid().isIn(location) &&
                    dungeonRoom.getDungeon().getPlayers().contains(player.getUniqueId()) &&
                    dungeonRoom.getTemplate().getName().equals(getName())) {
                /*player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + getName() + "-" + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() +
                        ", dungeonID: " + dungeonRoom.getDungeon().getID() +
                        ", players in dungeon: " + dungeonRoom.getDungeon().getPlayers().size() +
                        ", players in current room " + dungeonRoom.getWatchers().size()));*/
                //player.sendMessage("You're in: " + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() + " template: " + dungeonRoom.getTemplate().getName() + " dungeonID: " + dungeonRoom.getDungeon().getID());
                /*List<String> names = new ArrayList<>();
                dungeonRoom.getWatchers().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null)
                        names.add(Bukkit.getPlayer(uuid).getName());
                });*/
                //player.sendMessage("Players: " + String.join(", ", names));
            }
        });
    }

    @Override
    public DungeonType getType() {
        return DungeonType.SAFE;
    }

    /*@Override
    public Map<Cuboid, Double> getBuilds() {
        final Map<Cuboid, Double> map = new HashMap<>();
        final Cuboid room1 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 33),
                new Location(Bukkit.getWorld("world"), -22, 72, 18));

        map.put(room1, 100.0);
        return map;
    }*/

    @Override
    public MonsterFrequency getMonstersFrequency() {
        return null;
    }
}
