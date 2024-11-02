package org.enissay.dungeonssim.handlers;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.*;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.party.DungeonPartyStatus;
import org.enissay.dungeonssim.dungeon.system.*;
import org.enissay.dungeonssim.dungeon.templates.RoomSerialization;
import org.enissay.dungeonssim.utils.Cuboid;
import org.enissay.dungeonssim.utils.FileUtils;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

    public static DungeonRoom getRoomFromCell(final DungeonGeneration.GridCell gridCell) {
        AtomicReference<DungeonRoom> ref = new AtomicReference<>();
        getDungeons().forEach(dungeon -> {
            ref.set(dungeon.getRooms().stream().filter(dungeonRoom -> dungeonRoom.getGridCell() != null &&
                    gridCell != null &&
                    dungeonRoom.getGridCell().getX() == gridCell.getX() &&
                    dungeonRoom.getGridCell().getY() == gridCell.getY()).findAny().orElse(null));
        });
        return ref.get();
    }

    public static boolean isPlayerInADungeon(final Player player) {
        return getDungeons().stream()
                .filter(dungeon -> dungeon.getPlayers().contains(player.getUniqueId()))
                .toList().size() > 0;
    }

    public static void removeDungeon(final Dungeon dungeon) {
        RoomPasting.getSessionsFor(dungeon).forEach(editSession -> {
            try (EditSession newSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")))) {
                if (editSession != null)
                    editSession.undo(newSession);
            }
        });
        dungeon.getPlayers().forEach(player -> {
            if (Bukkit.getOfflinePlayer(player).isOnline()) {
                Bukkit.getPlayer(player).teleport(DungeonsSim.getInstance().getSpawnLocation());
                Bukkit.getPlayer(player).getInventory().setItemInOffHand(null);
            }
        });
        dungeon.removeAllHolograms();
        dungeon.removeBossbar();
        dungeon.removeAllMobs();
        dungeon.removeAllNPCs();

        dungeon.stopSongs();

        RoomPasting.getSessionsFor(dungeon).clear();
        PuzzleHandler.resetPuzzles(dungeon);
        dungeons.remove(dungeon);
    }

    public static void register(final DungeonTemplate... templates) {
        EventManager.on(PlayerQuitEvent.class, event -> {
            final Player player = event.getPlayer();
            final DungeonParty party = PartyHandler.getPartyOf(player.getUniqueId());
            if (party != null) {
                party.sendMessageToMembers("&cParty Member &b" + player.getName() + "&c has left the server.");
                if (party.getStatus() == DungeonPartyStatus.PLAYING &&
                        party.getDungeon() != null) {
                    List<Player> connectedPlayers = new ArrayList<>();
                    party.getPlayers().forEach((uuid, role) -> {
                        final OfflinePlayer partyPlayer = Bukkit.getOfflinePlayer(uuid);
                        if (partyPlayer.isOnline()) connectedPlayers.add(partyPlayer.getPlayer());
                    });
                    party.getDungeon().removePlayer(player.getUniqueId());
                    if (party.getDungeon().getPlayers().size() == 0) {
                        removeDungeon(party.getDungeon());
                        if (party.getPlayers().size() >= party.getMaxPlayers())
                            party.setStatus(DungeonPartyStatus.FULL);
                        else party.setStatus(DungeonPartyStatus.LOBBY);
                    }
                }
            }if (isPlayerInADungeon(player) && party == null){
                final Dungeon dungeon = getDungeonOf(player.getUniqueId());
                if (dungeon != null) {
                    dungeon.removePlayer(player.getUniqueId());
                    if (dungeon.getPlayers().size() == 0) {
                        removeDungeon(dungeon);
                    }
                }
            }
        });

        EventManager.on(PlayerJoinEvent.class, event -> {
            final Player player = event.getPlayer();
            final DungeonParty party = PartyHandler.getPartyOf(player.getUniqueId());
            if (party != null) party.sendMessageToMembers("&aParty Member &b" + player.getName() + "&a has joined the server.");
        });
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
                List<String> offlineNames = new ArrayList<>();
                dungeonRoom.getWatchers().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null)
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
                    if (Bukkit.getPlayer(uuid) != null)
                        names.add(Bukkit.getPlayer(uuid).getName());
                });
                if (!dungeonRoom.getCuboid().isIn(event.getTo())) {
                    dungeonRoom.getWatchers().remove(player.getUniqueId());
                    //Bukkit.broadcastMessage("Removed " + Bukkit.getPlayer(player.getUniqueId()).getName());
                }
            }
        });

        EventManager.on(PlayerInteractEvent.class, event -> {
            final Player player = event.getPlayer();
            final Block block = event.getClickedBlock();
            if (block != null && block.getType()!= null) {
                final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(block.getLocation());
                if (block.getType() == Material.BLACK_WOOL &&
                        dungeonRoom != null &&
                        dungeonRoom.getCuboid().isIn(block.getLocation()) &&
                        dungeonRoom.getDungeon().getPlayers().contains(player.getUniqueId())) {
                    final Cuboid cuboid = dungeonRoom.getCuboid();
                    CuboidTest newCuboid = new CuboidTest(cuboid.getPoint1(), cuboid.getPoint2());
                    for (int i = 0; i < dungeonRoom.getGridCell().getDoors().length; i++) {
                        if (DungeonParser.getOpposite(dungeonRoom.getDungeon().getDungeonGeneration(), i, dungeonRoom.getGridCell()) != null) {
                            final DungeonRoom opposite = DungeonHandler.getRoomFromCell(DungeonParser.getOpposite(dungeonRoom.getDungeon().getDungeonGeneration(), i, dungeonRoom.getGridCell()));
                            CuboidTest wall = newCuboid.getWalls()[i];
                            if (wall.contains(block)) {
                                /*player.sendMessage("Detected a door! ");
                                player.sendMessage("Room: " + ChatColor.YELLOW + dungeonRoom.getTemplate().getName() + "-" + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() +
                                        ", dungeonID: " + dungeonRoom.getDungeon().getID() +
                                        ", players in dungeon: " + dungeonRoom.getDungeon().getPlayers().size() +
                                        ", players in current room " + dungeonRoom.getWatchers().size());
                                player.sendMessage("");*/
                                if (opposite != null) {
                                    /*player.sendMessage("Opposite Room: " + ChatColor.GREEN + opposite.getTemplate().getName() + "-" + opposite.getRoomName() + "#" + opposite.getID() +
                                            ", dungeonID: " + opposite.getDungeon().getID() +
                                            ", players in dungeon: " + opposite.getDungeon().getPlayers().size() +
                                            ", players in current room " + opposite.getWatchers().size());*/
                                    if (dungeonRoom.getDungeon().getDungeonGeneration().getKeyRooms().contains(opposite.getGridCell())){
                                        String type = "&edoor.";
                                        int keyRooms = dungeonRoom.getDungeon().getDungeonGeneration().getKeyRooms().size();
                                        int openedRooms = dungeonRoom.getDungeon().getRoomsOpened().size();
                                        boolean hasBossKey = dungeonRoom.getDungeon().hasBossKey();
                                        int remaining = keyRooms - openedRooms;
                                        if (opposite.getRoomName().contains("RARE")) type = "&5&lRARE &edoor.";
                                        else if (opposite.getTemplate().getName().contains("BOSS")) type = "&c&lBOSS &edoor.";
                                        if ((/*!hasBossKey && */remaining > 1 && !opposite.getTemplate().getName().contains("BOSS")) ||
                                                (hasBossKey && remaining == 1 && opposite.getTemplate().getName().contains("BOSS"))) {
                                            DungeonParser.openAllKeyDoors(opposite);
                                            dungeonRoom.getDungeon().addRoomOpened(opposite);

                                            //player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1f, 0.8f);
                                            MessageUtils.broadcast("&b" + player.getName() + " &eopened a " + type + " &7(&b" + dungeonRoom.getDungeon().getRoomsOpened().size() + "&8/&3" + keyRooms + "&7)", MessageUtils.BroadcastType.SUB_TITLE, MessageUtils.TargetType.DUNGEON, dungeonRoom.getDungeon());

                                            if (opposite.getTemplate().getName().contains("BOSS")) {
                                                MessageUtils.broadcastSound(Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1f, 0.6f, MessageUtils.TargetType.DUNGEON, dungeonRoom.getDungeon());
                                            }else {
                                                MessageUtils.broadcastSound(Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1f, 0.6f, MessageUtils.TargetType.DUNGEON, dungeonRoom.getDungeon());
                                            }
                                            //dungeonRoom.getDungeon().broadcast("&b" + player.getName() + " &eopened a " + type + " &e(&b" + dungeonRoom.getDungeon().getRoomsOpened().size() + "&8/&3" + keyRooms + "&7)", Dungeon.BroadcastType.SUB_TITLE);
                                        }else if (remaining > 1 && opposite.getTemplate().getName().contains("BOSS")) {
                                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
                                            player.sendMessage(ChatColor.RED + "You can't open this door yet! " + (remaining - 1) + " remaining doors.");
                                        }else if (!hasBossKey && opposite.getTemplate().getName().contains("BOSS")) {
                                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
                                            player.sendMessage(ChatColor.RED + "You can't open this door yet! You need the boss's room key.");
                                        }
                                    }
                                }
                                /*List<String> keyRooms = new LinkedList<>();
                                dungeonRoom.getDungeon().getDungeonGeneration().getKeyRooms().forEach(gridCell -> {
                                    keyRooms.add(DungeonHandler.getRoomFromCell(gridCell).getTemplate().getName() + "-" + DungeonHandler.getRoomFromCell(gridCell).getRoomName());
                                });
                                player.sendMessage("Rooms that requires key: " + String.join(", ", keyRooms));*/
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        });

        Arrays.asList(templates).forEach(temp -> {
            temp.initEvents();
            System.out.println("Registered events for " + temp.getName());
            templateList.add(temp);
            System.out.println("Added " + temp.getName());
            /*temp.getRoomsFromTemplate().keySet().forEach(room -> {
                final CuboidTest cuboid = new CuboidTest(TempDungeonBuildsManager.roomLocationToBukkit(DungeonHandler.loadRoom(temp.getName(), room).getLocation1()),
                        TempDungeonBuildsManager.roomLocationToBukkit(DungeonHandler.loadRoom(temp.getName(), room).getLocation2()));
                StructureBlockLibApi.INSTANCE
                        .saveStructure(DungeonsSim.getInstance())
                        .at(cuboid.getLowerCorner())
                        .sizeX(cuboid.getSizeX())
                        .sizeY(cuboid.getSizeY())
                        .sizeZ(cuboid.getSizeZ())
                        .restriction(StructureRestriction.UNLIMITED)  // See JavaDoc for default values.
                        .saveToWorld("world", "me", temp.getName() + "-" + room)
                        .onException(e -> DungeonsSim.getInstance().getLogger().log(Level.SEVERE, "Failed to save structure.", e))
                        .onResult(e -> DungeonsSim.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "Saved structure " + temp.getName() + "-" + room));

            });*/
        });
    }

    public static DungeonRoom getRoomFromLocation(final Location location) {
        AtomicReference<DungeonRoom> rooms = new AtomicReference<>();
        dungeons.forEach(dungeon -> {
            //Bukkit.broadcastMessage(dungeon.getID() + " rooms: " + dungeon.getRooms().size());
            dungeon.getRooms().forEach(room -> {
                //Bukkit.broadcastMessage("room " + room.getRoomName() + " " + room.getTemplate().getName() + " " + room.getCuboid().isIn(location));
                if (room!= null && room.getCuboid() != null && room.getCuboid().isIn(location)) rooms.set(room);
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
