package org.enissay.dungeonssim.commands;

import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuildsManager;
import org.enissay.dungeonssim.dungeon.system.RoomPasting;
import org.enissay.dungeonssim.dungeon.templates.RoomRotation;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.List;

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


                EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                        .checkMemory(true)
                        //.forceWNA()
                        .allowedRegionsEverywhere()
                        .world(BukkitAdapter.adapt(Bukkit.getWorld("world")))
                        //.limitUnlimited()
                        .relightMode(RelightMode.ALL)
                        //.maxBlocks(1)
                        .build();
                RoomPasting roomPasting = new RoomPasting(tempDungeonBuilds, spawnLocation, RoomRotation.valueOf(rotation),
                        new int[]{0, 0, 0, 0}, editSession);//LEFT, RIGHT, DOWN, UP
                roomPasting.paste(null, cuboid.getXWidth());
                //roomPasting.pasteNewMethod(cuboid.getXWidth());
                //roomPasting.pasteWithJigsaw(null, roomName, cuboid.getXWidth());
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
