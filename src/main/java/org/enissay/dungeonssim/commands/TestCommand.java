package org.enissay.dungeonssim.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.system.*;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.ItemUtils;

import java.time.Instant;
import java.util.*;

@Command(name = "test")
public class TestCommand {
    private Random random = new Random();

    private void sendInfo(final Player player, final String msg) {
        sendMessage(player, "&7" + msg);
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

    @Execute
    void test(@Context CommandSender sender, @Arg int MIN_ROOM, @Arg int MAX_ROOM, @OptionalArg long seed) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final LinkedList<Player> players = new LinkedList<>();
            players.add(player);

            sendInfo(player, "Creating dungeon instance...");
            long startTime = System.currentTimeMillis();

            final Dungeon dungeon = new Dungeon(DungeonHandler.getDungeons().size() + 1,
                    players.stream().map(Player::getUniqueId).toList(), DungeonDifficulty.EASY, Instant.now());

            final DungeonGeneration dungeonGeneration = new DungeonGeneration(dungeon);
            dungeonGeneration.setGridBlocks(33)
                    .setGridOptions(6, 6)
                    .setMinRooms(MIN_ROOM)
                    .setMaxRooms(MAX_ROOM)
                    .build();

            sendInfo(player, "Dungeon algorithm...");

            final double DUNGEONS_GAP = 1.1;
            final Location loc = new Location(player.getWorld(), 19013.5 + ((DungeonHandler.getDungeons().size()) * (dungeonGeneration.getGridWidth()) * dungeonGeneration.getGridBlocks() * DUNGEONS_GAP), 141, 20846.5);
            DungeonParser.parse(dungeon, dungeonGeneration, loc/*player.getLocation()*/);

            final HashMap<DungeonGeneration.GridCell, String> roomNames = new HashMap<>();
            final HashMap<DungeonGeneration.GridCell, String> roomTemplates = new HashMap<>();

            dungeon.getRooms().forEach(room -> {
                roomNames.put(room.getGridCell(), room.getRoomName());
                roomTemplates.put(room.getGridCell(), room.getTemplate().getName());
            });

            final Generator2D generator2D = new Generator2D(dungeonGeneration, dungeonGeneration.getGridHeight(), dungeonGeneration.getGridWidth(), dungeonGeneration.getGridMap(), dungeonGeneration.getGridBlocks(), roomNames, roomTemplates);
            generator2D.generateMap(loc, player.getWorld());

            final ItemStack item = ItemUtils.item(Material.FILLED_MAP, "dungeon#" + dungeon.getID(), "");
            MapMeta meta = (MapMeta) item.getItemMeta();
            MapView view = Bukkit.createMap(player.getWorld());

            view.getRenderers().clear();
            view.addRenderer(generator2D);
            view.setTrackingPosition(true);
            view.setUnlimitedTracking(true);
            view.setCenterX(loc.getBlockX());
            view.setCenterZ(loc.getBlockZ());
            view.setScale(MapView.Scale.CLOSE);
            meta.setMapView(view);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);

            long endTime = System.currentTimeMillis();
            long deltaTime = endTime - startTime;

            sendNormal(player, "Dungeon generated! took " + deltaTime + "ms" + " (" + deltaTime * Math.pow(10, -3) + "s)");
            final DungeonRoom room = dungeon.getRooms().stream().filter(dungeonRoom -> dungeonRoom.getTemplate().getName().equals("SPAWN_ROOM")).findAny().orElse(null);
            if (room != null) {
                final TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(room.getTemplate().getName(), room.getRoomName());
                dungeon.getPlayers().forEach(playerUUID -> {
                    final Player dungeonPlayer = Bukkit.getPlayer(playerUUID);
                    if (tempDungeonBuilds.getRoomLocations().get("spawnLocation") != null) {
                        dungeonPlayer.teleport(room.getLocationFromTemplate("spawnLocation"));
                    }else dungeonPlayer.teleport(room.getCuboid().getCenter());
                });
            }
        }
    }
}
