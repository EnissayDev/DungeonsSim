package org.enissay.dungeonssim.commands.dungeonloc;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.dungeon.CuboidTest;
import org.enissay.dungeonssim.dungeon.templates.RoomLocation;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.Cuboid;
import org.enissay.dungeonssim.utils.ItemUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Command(name = "dungeonloc")
public class DungeonLocationsCommand {

    @Execute(name = "addloc")
    void addloc(@Context CommandSender sender, @Arg String templateName, @Arg String roomName, @Arg String roomLocation) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (roomName != null && templateName != null && DungeonHandler.getTemplate(templateName) != null) {
                final TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(templateName, roomName);
                tempDungeonBuilds.getRoomLocations().put(roomLocation, new RoomLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
                DungeonHandler.publishRoom(tempDungeonBuilds);

                player.sendMessage(ChatColor.GREEN + "Successfully added Location " + roomLocation + " to " + templateName + "-" + roomName);
            }
        }
    }
    @Execute(name = "addbuild")
    void generate(@Context CommandSender sender, @Arg String templateName, @Arg String roomName, @Arg double chance/*, @Arg int port*/) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (roomName != null && templateName != null && DungeonHandler.getTemplate(templateName) != null) {
                TempDungeonBuildsManager.put(player.getUniqueId(), new TempDungeonBuilds(templateName, roomName, 0, chance));

                final ItemStack item = ItemUtils.item(Material.STICK, ChatColor.YELLOW + "Dungeon Room Selector", "",ChatColor.GRAY + "Like worldedit selection");

                player.getInventory().addItem(item);
                player.sendMessage(ChatColor.GREEN + "Started selection session for dungeon: " + templateName + "/" + roomName);
            }
        }
    }

    @Execute(name = "confirmbuild")
    void confirm(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            TempDungeonBuilds temp = TempDungeonBuildsManager.get(player.getUniqueId());
            if (temp != null&& temp.getLocation1() != null && temp.getLocation2() != null) {
                //Save in json file: ../template/name_of_map.json
                Cuboid cuboid = new Cuboid(new Location(player.getWorld(), temp.getLocation1().getX(), temp.getLocation1().getY(), temp.getLocation1().getZ()),
                        new Location(player.getWorld(), temp.getLocation2().getX(), temp.getLocation2().getY(), temp.getLocation2().getZ()));
                CuboidTest walls1 = new CuboidTest(cuboid.getPoint1(), cuboid.getPoint2());
                CuboidTest doorLoc = walls1.getWalls()[0];
                AtomicReference<Integer> minY = new AtomicReference<>((int) 0);
                AtomicBoolean passed = new AtomicBoolean(false);
                doorLoc.forEach(doorL -> {
                    final Block block = doorL.getBlock();
                    if (block.getType() == Material.LIME_WOOL) {
                        minY.set(block.getY());
                        passed.set(true);
                    }
                });
                if (!passed.get()) {
                    minY.set(walls1.getUpperY());
                }
                temp.setMinY(minY.get());
                //Bukkit.broadcastMessage(temp.getMinY() + "");
                DungeonHandler.publishRoom(temp);
            }else {
                player.sendMessage(ChatColor.RED + "Something is missing...");
            }
            //Clear the session
            TempDungeonBuildsManager.clear(player.getUniqueId());

            player.sendMessage(ChatColor.GREEN + "Finished.");

        }
    }
}
