package org.enissay.dungeonssim.commands;

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
import org.checkerframework.checker.units.qual.C;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuildsManager;
import org.enissay.dungeonssim.dungeon.RoomPasting;
import org.enissay.dungeonssim.dungeon.RoomRotation;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.Cuboid;
import org.enissay.dungeonssim.utils.FileUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Command(name = "dungeongen")
public class GenerateCommand {
    @Execute
    void generate(@Context CommandSender sender, @Arg String templateName, @Arg String roomName, @Arg String rotation) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (roomName != null) {
                player.sendMessage(ChatColor.GRAY + "Trying to find the room " + ChatColor.YELLOW + roomName);
                final TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(templateName, roomName);
                final Cuboid cuboid = new Cuboid(TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation1()), TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation2()));
                final List<Block> blockList = cuboid.blocksListed();

                player.sendMessage(ChatColor.GREEN + "Processing " + ChatColor.GREEN + blockList.size()+ ChatColor.GREEN + " blocks");

                final Location spawnLocation = player.getLocation();
                /*int blocksPerTick = 200;
                AtomicInteger taskId = new AtomicInteger();

                Iterator<Block> it = blockList.iterator();

                taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonsSim.getInstance(), () -> {
                    for(int i=0; i<blocksPerTick; i++) {
                        if(!it.hasNext()){
                            Bukkit.getScheduler().cancelTask(taskId.get());
                            break;
                        }
                        Block sourceBlock = it.next();
                        Block targetBlock = sourceBlock.getWorld().getBlockAt(sourceBlock.getX() + spawnLocation.getBlockX(), sourceBlock.getY(), sourceBlock.getZ() + spawnLocation.getBlockZ());

                        targetBlock.setBlockData(sourceBlock.getBlockData(), false);
                    }

                }, 1, 1));*/

                /*

                 */
                RoomPasting roomPasting = new RoomPasting(tempDungeonBuilds, spawnLocation, 100, RoomRotation.valueOf(rotation),
                        new int[]{0, 0, 0, 0});//LEFT, RIGHT, DOWN, UP
                roomPasting.pasteTest(null, cuboid.getXWidth(), templateName.contains("DOOR"));
                //roomPasting.setTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonsSim.getInstance(), roomPasting, 1, 1));

                /*while (roomPasting != null && !Bukkit.getScheduler().isCurrentlyRunning(roomPasting.getTaskID())) {
                    player.sendMessage(ChatColor.GREEN + "Finished generating ID: " + roomPasting.getTaskID());
                    roomPasting = null;
                }*/

                /*RoomPasting roomPasting = new RoomPasting(tempDungeonBuilds, spawnLocation);
                roomPasting.paste(200, tempDungeonBuilds);
                while (roomPasting != null && !Bukkit.getScheduler().isCurrentlyRunning(roomPasting.getTaskID())) {
                    player.sendMessage(ChatColor.GREEN + "Finished generating ID: " + roomPasting.getTaskID());
                    roomPasting = null;
                }*/
            }
        }
    }
}
