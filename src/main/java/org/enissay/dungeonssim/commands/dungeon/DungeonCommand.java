package org.enissay.dungeonssim.commands.dungeon;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.dungeon.system.DungeonGeneration;
import org.enissay.dungeonssim.dungeon.system.DungeonParser;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.templates.puzzle.IPuzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleType;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PuzzleHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Command(name = "dungeon")
public class DungeonCommand {

    private void sendInfo(final Player player, final String msg) {
        sendMessage(player, "&7" + msg);
    }

    private void sendStat(final Player player, final String key, final int value) {
        sendMessage(player, "&7" + key + ": &e" + value);
    }

    private void sendStat(final Player player, final String key, final String value) {
        sendMessage(player, "&7" + key + ": &e" + value);
    }

    private void sendNormal(final Player player, final String msg) {
        sendMessage(player, "&a" + msg);
    }

    private void sendError(final Player player, final String msg) {
        sendMessage(player, "&c" + msg);
    }

    private void sendMessage(final Player player, final String msg) {
        player.sendMessage(msg.replace('&', '§'));
    }

    @Execute(name = "create")
    void create(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            //if (dungeon == null)
        }
    }

    @Execute(name = "open")
    void open(@Context CommandSender sender, @Arg Integer id) {
        final Player player = (Player) sender;
        final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
        final DungeonGeneration dungeonGeneration = dungeon.getDungeonGeneration();
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "LEFT");
        map.put(1, "RIGHT");
        map.put(2, "DOWN");
        map.put(3, "UP");
        if (dungeon == null || dungeonGeneration == null) sendError(player, "You are not in a dungeon");
        else {
            DungeonGeneration.GridCell target = dungeonGeneration.getKeyRooms().get(id);
            DungeonRoom room = DungeonHandler.getRoomFromCell(target);
            sendStat(player, "Target", target.getX() + " " + target.getY() + " ");
            DungeonParser.openAllKeyDoors(room);
        }
    }

    @Execute(name = "info")
    void info(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            final DungeonGeneration dungeonGeneration = dungeon.getDungeonGeneration();
            Map<Integer, String> map = new HashMap<>();
            map.put(0, "LEFT");
            map.put(1, "RIGHT");
            map.put(2, "DOWN");
            map.put(3, "UP");
            if (dungeon == null || dungeonGeneration == null) sendError(player, "You are not in a dungeon");
            else {
                sendStat(player, "Dungeon ID", dungeon.getID());
                sendStat(player, "Rooms", dungeon.getRooms().size());
                sendStat(player, "Difficulty", dungeon.getDungeonDifficulty().name());
                sendStat(player, "Players", dungeon.getPlayers().size());
                AtomicInteger keyDoors = new AtomicInteger();
                //AtomicInteger keyRooms = new AtomicInteger();
                List<DungeonGeneration.GridCell> visited = new ArrayList<>();
                //List<DungeonGeneration.GridCell> visitedRoom = new ArrayList<>();
                dungeon.getRooms().forEach(dungeonRoom -> {
                    for (int i = 0; i < dungeonRoom.getGridCell().getDoors().length; i++) {
                        if (dungeonRoom.getGridCell().getDoors()[i] == 2 && !visited.contains(DungeonParser.getOpposite(dungeonGeneration, i, dungeonRoom.getGridCell()))) {
                            visited.add(dungeonRoom.getGridCell());
                            keyDoors.getAndIncrement();
                            //sendStat(player, dungeonRoom.getTemplate().getName() + "-" + dungeonRoom.getRoomName(), map.get(i));
                            break;
                        }
                        /*if (dungeonRoom.getGridCell().getDoors()[i] == 2 && !visitedRoom.contains(dungeonRoom.getGridCell())) {
                            visitedRoom.add(dungeonRoom.getGridCell());
                            visitedRoom.add(DungeonParser.getOpposite(dungeonGeneration, i, dungeonRoom.getGridCell()));
                            keyRooms.getAndIncrement();
                            //sendStat(player, dungeonRoom.getTemplate().getName() + "-" + dungeonRoom.getRoomName(), map.get(i));
                            break;
                        }*/
                    }
                });
                sendStat(player, "Doors that requires key", keyDoors.get());
                sendStat(player, "Rooms that requires key", dungeonGeneration.getKeyRooms().size() /*+ " " + keyRooms.get()*/);
                List<String> openedRooms = new ArrayList<>();
                dungeon.getRoomsOpened().forEach(dungeonRoom -> {
                    openedRooms.add(dungeonRoom.getTemplate().getName() + "-" + dungeonRoom.getRoomName());
                });
                sendStat(player, "Rooms opened", dungeon.getRoomsOpened().size() + " -> " + String.join(", ", openedRooms));
            }
        }
    }

    @Execute(name = "puzzle sim")
    void sim(@Context CommandSender sender, @Arg String puzzleName) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            final DungeonGeneration dungeonGeneration = dungeon.getDungeonGeneration();
            if (dungeon == null || dungeonGeneration == null) sendError(player, "You are not in a dungeon.");
            else if (PuzzleType.valueOf(puzzleName) == null) sendError(player, "This puzzle does not exist.");
            else {
                final IPuzzle puzzle = PuzzleHandler.getPuzzleTemplate(PuzzleType.valueOf(puzzleName));
                DungeonRoom puzzleRoom = null;
                for (DungeonRoom room : dungeon.getRooms()) {
                    if (room.getPuzzle() != null && room.getPuzzle().getPuzzleType() == puzzle.getType()) puzzleRoom = room;
                }
                if (puzzleRoom != null) {
                    sendNormal(player, "Simulating the completion of puzzle: " + puzzle.getType().name());
                    puzzle.onCompletion(puzzleRoom);
                }else sendError(player, "Couldn't find a room with that puzzle.");
            }
        }
    }


    @Execute(name = "remove")
    void remove(@Context CommandSender sender, @Arg Integer id) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Dungeon dungeon = DungeonHandler.getDungeons().get(id-1);
            if (dungeon != null) {
                DungeonHandler.removeDungeon(dungeon);
            }
            //if (dungeon == null)
        }
    }

    @Execute(name = "invite")
    void invite(@Context CommandSender sender, @Arg Player target) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            if (dungeon == null) {
                player.sendMessage(ChatColor.RED + "You don't have a dungeon party");
                return;
            }
            if (target != null && dungeon.getPlayers().size() > 0 &&
                    !target.getUniqueId().equals(player.getUniqueId()) &&
                    !dungeon.getPlayers().contains(target.getUniqueId())) {
                dungeon.addPlayer(target.getUniqueId());
                target.sendMessage(ChatColor.GREEN + "You forcefully got invited cuh");
            }else player.sendMessage(ChatColor.RED + "Something isn't right");
        }
    }

    @Execute(name = "list")
    void list(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (DungeonHandler.getDungeons().size() > 0) {
                player.sendMessage(ChatColor.GREEN + "DUNGEONS: " + DungeonHandler.getDungeons().size());

                DungeonHandler.getDungeons().forEach(dungeon -> {
                    List<String> names = new ArrayList<>();
                    dungeon.getPlayers().forEach(uuid -> {
                        names.add(Bukkit.getPlayer(uuid).getName());
                    });
                    player.sendMessage(ChatColor.GREEN + "-> DUNGEON" + ChatColor.AQUA + "#" + dungeon.getID() + ChatColor.GREEN +
                            " - Rooms: " + ChatColor.YELLOW + dungeon.getRooms().size() + ChatColor.GREEN +
                            " - Players: " + ChatColor.YELLOW + String.join(",", names));
                });
            }else player.sendMessage(ChatColor.RED + "No dungeons available");
        }
    }

    @Execute(name = "generate")
    void generate(@Context CommandSender sender, @Arg Player target) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
        }
    }
}
