package org.enissay.dungeonssim.dungeon.templates.impl;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.EventManager;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.templates.MonsterFrequency;
import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleTemplate;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleType;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.entities.hostile.Necromancer;
import org.enissay.dungeonssim.entities.hostile.skeleton.UndeadSkeleton;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PuzzleHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleRoom implements DungeonTemplate, PuzzleTemplate {
    @Override
    public String getName() {
        return "PUZZLE_ROOM";
    }

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public int getMinRooms() {
        return 1;
    }

    @Override
    public boolean hasUniqueRooms() {
        return true;
    }

    @Override
    public int getMaxRooms() {
        return 2;
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
                //ChatColor cl = ChatColor.AQUA;
                //Puzzle puzzle = PuzzleHandler.getPuzzle(dungeonRoom);
                /*PuzzleType puzzle = null;
                if (dungeonRoom.getTemplate() instanceof PuzzleTemplate) {
                    PuzzleTemplate puzzleTemplate = (PuzzleTemplate) dungeonRoom.getTemplate();
                    if (puzzleTemplate.getPuzzles().containsKey(dungeonRoom.getRoomName())) {
                        puzzle = puzzleTemplate.getPuzzles().get(dungeonRoom.getRoomName());
                    }
                }*/
                /*player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(cl + getName() + "-" + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() +
                        ", dungeonID: " + dungeonRoom.getDungeon().getID() +
                        ", players in dungeon: " + dungeonRoom.getDungeon().getPlayers().size() +
                        ", players in current room " + dungeonRoom.getWatchers().size() +
                        ", puzzle: " + (puzzle != null ? puzzle.getPuzzleType().name() : "?")));*/
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
    public MonsterFrequency getMonstersFrequency() {

        // Add specific room frequencies
        //monsterFrequency.addFrequency(Necromancer.class, this, "ROOM_2", 100.0);
        return null;
    }

    @Override
    public Map<String, PuzzleType> getPuzzles() {
        Map<String, PuzzleType> puzzles = new HashMap<>();
        puzzles.put("ROOM_1", PuzzleType.CUBE_ROTATION);
        puzzles.put("ROOM_2", PuzzleType.SOUL_COLLECTING);
        return puzzles;
    }
}
