package org.enissay.dungeonssim.dungeon.templates;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.enissay.dungeonssim.dungeon.DungeonRoom;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.EventManager;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalRoom implements DungeonTemplate {
    @Override
    public String getName() {
        return "NORMAL_ROOM";
    }

    @Override
    public int getID() {
        return 0;
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
                ChatColor cl = ChatColor.BLUE;
                if (dungeonRoom.getRoomName().equals("ROOM_RARE")) cl = ChatColor.DARK_PURPLE;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(cl + getName() + "-" + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() +
                        ", dungeonID: " + dungeonRoom.getDungeon().getID() +
                        ", players in dungeon: " + dungeonRoom.getDungeon().getPlayers().size() +
                        ", players in current room " + dungeonRoom.getWatchers().size()));
                //player.sendMessage("You're in: " + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() + " template: " + dungeonRoom.getTemplate().getName() + " dungeonID: " + dungeonRoom.getDungeon().getID());
                List<String> names = new ArrayList<>();
                dungeonRoom.getWatchers().forEach(uuid -> {
                    names.add(Bukkit.getPlayer(uuid).getName());
                });
                //player.sendMessage("Players: " + String.join(", ", names));
            }
        });
    }

    /*@Override
    public Map<Cuboid, Double> getBuilds() {
        final Map<Cuboid, Double> map = new HashMap<>();
        final Cuboid room1 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 54),
                new Location(Bukkit.getWorld("world"), -22, 72, 39));
        final Cuboid room2 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 54),
                new Location(Bukkit.getWorld("world"), -22, 72, 39));

        map.put(room1, 50.0);
        map.put(room2, 50.0);
        return map;
    }*/


    @Override
    public Map<Entity, Double> getMonstersFrequency() {
        return null;
    }
}
