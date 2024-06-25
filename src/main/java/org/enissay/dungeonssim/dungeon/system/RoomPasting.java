package org.enissay.dungeonssim.dungeon.system;

import com.fastasyncworldedit.bukkit.regions.plotsquared.FaweDelegateSchematicHandler;
import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.fastasyncworldedit.core.limit.FaweLimit;
import com.github.shynixn.structureblocklib.api.bukkit.StructureBlockLibApi;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionBuilder;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuildsManager;
import org.enissay.dungeonssim.dungeon.CuboidTest;
import org.enissay.dungeonssim.dungeon.templates.RoomRotation;
import org.enissay.dungeonssim.utils.BlockChanger;
import org.enissay.dungeonssim.utils.Cuboid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class RoomPasting {
    public TempDungeonBuilds tempDungeonBuilds;
    public Location spawnLocation;
    public Cuboid cuboid;
    public RoomRotation rotation;
    public int[] doors;
    private EditSession editSession;

    private static Map<Dungeon, List<EditSession>> sessions = new HashMap<>();

    public RoomPasting(TempDungeonBuilds tempDungeonBuilds, Location spawnLocation, RoomRotation rotation, int[] doors) {
        this.tempDungeonBuilds = tempDungeonBuilds;
        this.spawnLocation = spawnLocation;
        this.cuboid = new Cuboid(TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation1()), TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation2()));
        this.rotation = rotation;
        this.doors = doors;
    }

    public RoomPasting(TempDungeonBuilds tempDungeonBuilds, Location spawnLocation, RoomRotation rotation, int[] doors, EditSession editSession) {
        this.tempDungeonBuilds = tempDungeonBuilds;
        this.spawnLocation = spawnLocation;
        this.cuboid = new Cuboid(TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation1()), TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation2()));
        this.rotation = rotation;
        this.doors = doors;
        this.editSession = editSession;
    }

    /*public String betterLoc(final Location location) {
        return location != null ? "(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")" : "undefined";
    }*/

    public Location rotationCorner(Location location, RoomRotation rotation, int gridBlocks) {
        return switch (rotation) {
            case EAST -> new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + gridBlocks - 1);
            case SOUTH -> new Location(location.getWorld(), location.getX() + gridBlocks - 1, location.getY(), location.getZ() + gridBlocks - 1);
            case WEST -> new Location(location.getWorld(), location.getX() + gridBlocks - 1, location.getY(), location.getZ());
            default -> location;
        };
    }

    public static List<EditSession> getSessionsFor(Dungeon dungeon) {
        return sessions.get(dungeon);
    }

    public Cuboid paste(Dungeon dungeon, int gridBlocks) {

        BlockVector3 pos1Vector = BlockVector3.at(cuboid.getPoint1().getBlockX(), cuboid.getPoint1().getY(), cuboid.getPoint1().getZ());
        BlockVector3 pos2Vector = BlockVector3.at(cuboid.getPoint2().getBlockX(), cuboid.getPoint2().getY(), cuboid.getPoint2().getZ());
        CuboidRegion region = new CuboidRegion(pos1Vector, pos2Vector);

        final Location location = rotationCorner(spawnLocation, rotation, gridBlocks);

        int minY = tempDungeonBuilds.getMinY();
        BlockVector3 baseLocation = BlockVector3.at(location.getX(), (minY - 2*(minY - 75)), location.getZ());

        final Clipboard clipboard = DungeonParser.loadRoomSchematic(tempDungeonBuilds);
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

        Operation operation = clipboardHolder
                .createPaste(editSession)
                .to(baseLocation)
                .copyEntities(false)
                .build();

        Operations.complete(operation);

        if (dungeon != null) {
            List<EditSession> editSessions = getSessionsFor(dungeon);
            if (editSessions != null && !editSessions.contains(editSession)) {
                editSessions.add(editSession);
                sessions.put(dungeon, editSessions);
            }else {
                editSessions = new ArrayList<>();
                editSessions.add(editSession);
            }
            sessions.put(dungeon, editSessions);
        }

        editSession.flushQueue();

        /**
         * PUZZLES HANDLER
         */

        int diffX = region.getPos2().getX() - region.getPos1().getX();
        int diffY = region.getPos2().getY() - region.getPos1().getY();
        int diffZ = region.getPos2().getZ() - region.getPos1().getZ();
        CuboidTest newCuboid = new CuboidTest(BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation), BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation.add(diffX, diffY, diffZ)));

        for (int i = 0; i < 4; i++) {
            CuboidTest cuboidTest = newCuboid.getWalls()[i];
            switch (doors[i]) {
                //NORMAL CLOSING

                case 1:
                    cuboidTest.forEach(loc -> {
                        final Block block = loc.getBlock();
                        if (block.getType() == Material.LIME_WOOL) {
                            block.setType(Material.AIR);
                        }
                    });
                    break;
                //KEY DOOR CLOSING
                case 2:
                    cuboidTest.forEach(loc -> {
                        final Block block = loc.getBlock();
                        if (block.getType() == Material.LIME_WOOL) {
                            block.setType(Material.BLACK_WOOL);
                        }
                    });
                    break;
            }
        }
        return new Cuboid(BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation), BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation.add(diffX, diffY, diffZ)));
    }

    public static Clipboard fromCuboid(CuboidRegion region) {
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        ForwardExtentCopy copy = new ForwardExtentCopy(Objects.requireNonNull(region.getWorld()), region, clipboard, region.getMinimumPoint());
        copy.setCopyingEntities(false);
        return clipboard;
    }
}
