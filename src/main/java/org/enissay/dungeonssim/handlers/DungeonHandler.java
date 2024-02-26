package org.enissay.dungeonssim.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.*;
import org.enissay.dungeonssim.utils.FileUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DungeonHandler {

    private static LinkedList<Dungeon> dungeons = new LinkedList<>();
    private static LinkedList<DungeonTemplate> templateList = new LinkedList<>();

    public static DungeonTemplate getTemplateFromID(final int ID) {
        DungeonTemplate template = null;
        switch (ID) {
            case 1:
                template = DungeonHandler.getTemplate("NORMAL_ROOM");
                break;
            case 2:
                template = DungeonHandler.getTemplate("BOSS_ROOM");
                break;
            case 3:
                template = DungeonHandler.getTemplate("SPAWN_ROOM");
                break;

        }
        return template;
    }

    public static LinkedList<Dungeon> getDungeons() {
        return dungeons;
    }

    public static boolean addDungeon(final Dungeon dungeon) {
        if (!dungeons.contains(dungeon)) dungeons.add(dungeon);
        return dungeons.contains(dungeon);
    }

    public static DungeonTemplate getTemplateFromName(final String templateName) {
        return templateList.stream().filter(template ->
                template.getName().equals(templateName)).findFirst().get();
    }

    public static Dungeon getDungeonOf(final UUID player) {
        return dungeons.stream().filter(dungeon -> dungeon.getPlayers().contains(player)).findFirst().orElse(null);
    }

    public static void register(final DungeonTemplate... templates) {
        EventManager.on(PlayerTeleportEvent.class, event -> {
            //Bukkit.broadcastMessage("bingo!");
            final Player player = event.getPlayer();
            final Location location = event.getFrom();
            final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(location);
            if (dungeonRoom != null &&
                    dungeonRoom.getCuboid().isIn(location) &&
                    dungeonRoom.getDungeon().getPlayers().contains(player.getUniqueId())) {
                if (!dungeonRoom.getWatchers().contains(player.getUniqueId()))
                    dungeonRoom.addWatcher(player.getUniqueId());
                List<String> names = new ArrayList<>();
                dungeonRoom.getWatchers().forEach(uuid -> {
                    names.add(Bukkit.getPlayer(uuid).getName());
                });
                if (!dungeonRoom.getCuboid().isIn(event.getTo()) &&
                        dungeonRoom.getWatchers().contains(player.getUniqueId())) {
                    dungeonRoom.getWatchers().remove(player.getUniqueId());
                    //Bukkit.broadcastMessage("Removed " + Bukkit.getPlayer(player.getUniqueId()).getName());
                }
            }
            if (dungeonRoom != null &&
                    !dungeonRoom.getCuboid().isIn(event.getTo()) &&
                    dungeonRoom.getCuboid().isIn(event.getFrom()) &&
                    dungeonRoom.getWatchers().contains(player.getUniqueId())) {
                dungeonRoom.getWatchers().remove(player.getUniqueId());
                //Bukkit.broadcastMessage("Removed " + Bukkit.getPlayer(player.getUniqueId()).getName());
            }

        });
        EventManager.on(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Location location = event.getFrom();
            final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(location);
            if (dungeonRoom != null &&
                    dungeonRoom.getCuboid().isIn(location) &&
                    dungeonRoom.getDungeon().getPlayers().contains(player.getUniqueId())) {
                if (!dungeonRoom.getWatchers().contains(player.getUniqueId()))
                    dungeonRoom.addWatcher(player.getUniqueId());
                List<String> names = new ArrayList<>();
                dungeonRoom.getWatchers().forEach(uuid -> {
                    names.add(Bukkit.getPlayer(uuid).getName());
                });
                if (!dungeonRoom.getCuboid().isIn(event.getTo())) {
                    dungeonRoom.getWatchers().remove(player.getUniqueId());
                    //Bukkit.broadcastMessage("Removed " + Bukkit.getPlayer(player.getUniqueId()).getName());
                }
            }
        });

        Arrays.asList(templates).forEach(temp -> {
            temp.initEvents();
            System.out.println("Registered events for " + temp.getName());
            templateList.add(temp);
            System.out.println("Added " + temp.getName());
        });
    }

    public static DungeonRoom getRoomFromLocation(final Location location) {
        AtomicReference<DungeonRoom> rooms = new AtomicReference<>();
        dungeons.forEach(dungeon -> {
            //Bukkit.broadcastMessage(dungeon.getID() + " rooms: " + dungeon.getRooms().size());
            dungeon.getRooms().forEach(room -> {
                //Bukkit.broadcastMessage("room " + room.getRoomName() + " " + room.getTemplate().getName() + " " + room.getCuboid().isIn(location));
                if (room.getCuboid().isIn(location)) rooms.set(room);
            });
        });
        return rooms.get();
    }

    public static DungeonTemplate getTemplate(final String templateName) {
        return templateList.stream().filter(temp -> temp.getName().equals(templateName)).findFirst().get();
    }

    public static LinkedList<DungeonTemplate> getTemplateList() {
        return templateList;
    }

    public static void publishRoom(final TempDungeonBuilds tempDungeonBuilds) {
        final String json = new RoomSerialization().serialize(tempDungeonBuilds);
        FileUtils.save(new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/template/" + tempDungeonBuilds.getTemplateName() + "/" + tempDungeonBuilds.getName() + ".json"), json);
    }

    public static TempDungeonBuilds loadRoom(final String templateName, final String roomName) {
        final File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/template/" + templateName + "/" + roomName + ".json");
        final TempDungeonBuilds tempDungeonBuilds = new RoomSerialization().deserialize(FileUtils.loadContent(file));
        return tempDungeonBuilds;
    }

    public static List<TempDungeonBuilds> loadRooms(final String templateName) {
        final File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/template/" + templateName);
        List<TempDungeonBuilds> builds = new ArrayList<>();
        if (file != null && file.exists()) {
            final String[] mapsName = file.list();

            if (mapsName.length > 0) {

                for (String names : mapsName) {
                    names = names.replace(".json", "");
                    builds.add(loadRoom(templateName, names));
                }
            }
        }
        return builds;
    }
}
