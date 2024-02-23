package org.enissay.dungeonssim.dungeon;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuildsManager;
import org.enissay.dungeonssim.utils.Cuboid;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomPasting implements Runnable{
    private int taskID, blocksPerTick;
    public TempDungeonBuilds tempDungeonBuilds;
    public Location spawnLocation;
    public Cuboid cuboid;
    public List<Block> blockList;
    public Iterator<Block> it;
    public RoomRotation rotation;
    public int[] doors;

    public RoomPasting(TempDungeonBuilds tempDungeonBuilds, Location spawnLocation, int blocksPerTick, RoomRotation rotation, int[] doors) {
        this.tempDungeonBuilds = tempDungeonBuilds;
        this.spawnLocation = spawnLocation;
        this.blocksPerTick = blocksPerTick;
        this.cuboid = new Cuboid(TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation1()), TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation2()));
        this.blockList = cuboid.blocksListed();
        this.it = blockList.iterator();
        this.rotation = rotation;
        this.doors = doors;
    }

    /*public int paste(int blocksPerTick, TempDungeonBuilds tempDungeonBuilds) {
        AtomicInteger taskId = new AtomicInteger();
        final Cuboid cuboid = new Cuboid(TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation1()), TempDungeonBuildsManager.roomLocationToBukkit(tempDungeonBuilds.getLocation2()));
        final List<Block> blockList = cuboid.blocksListed();
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
        }, 1, 1));
        taskID = taskId.get();
        return taskId.get();
    }*/

    // Method to rotate a location around the spawnLocation
    /*private Location rotateLocation(Location location, RoomRotation rotation) {
        double angle = Math.toRadians(rotation.getRotationTheta()); // Convert rotation angle to radians
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        double xOffset = location.getX() - spawnLocation.getX();
        double zOffset = location.getZ() - spawnLocation.getZ();

        double rotatedX = xOffset * cosAngle - zOffset * sinAngle;
        double rotatedZ = xOffset * sinAngle + zOffset * cosAngle;

        double newX = spawnLocation.getX() + rotatedX;
        double newZ = spawnLocation.getZ() + rotatedZ;

        final Location newLoc = new Location(location.getWorld(), newX + xOffset, location.getY(), newZ + zOffset);
        return newLoc;
    }*/

    public String betterLoc(final Location location) {
        return location != null ? "(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")" : "undefined";
    }

    public Location rotationCorner(Location location, RoomRotation rotation, int gridBlocks) {
        return switch (rotation) {
            case EAST -> new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + gridBlocks - 1);
            case SOUTH -> new Location(location.getWorld(), location.getX() + gridBlocks - 1, location.getY(), location.getZ() + gridBlocks - 1);
            case WEST -> new Location(location.getWorld(), location.getX() + gridBlocks - 1, location.getY(), location.getZ());
            default -> location;
        };
    }

    public void pasteTest(int gridBlocks) {
        BlockVector3 pos1Vector = BlockVector3.at(cuboid.getPoint1().getBlockX(), cuboid.getPoint1().getY(), cuboid.getPoint1().getZ());
        BlockVector3 pos2Vector = BlockVector3.at(cuboid.getPoint2().getBlockX(), cuboid.getPoint2().getY(), cuboid.getPoint2().getZ());
        CuboidRegion region = new CuboidRegion(pos1Vector, pos2Vector);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                BukkitAdapter.adapt(spawnLocation.getWorld()), region, clipboard, region.getMinimumPoint()
        );
        //forwardExtentCopy.setTransform(forwardExtentCopy.getTransform().combine(new AffineTransform().rotateY(rotation.getRotationTheta())));

        Operations.complete(forwardExtentCopy);

        final Location location = rotationCorner(spawnLocation, rotation, gridBlocks);
        BlockVector3 baseLocation = BlockVector3.at(location.getX(), region.getMaximumY(), location.getZ());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(spawnLocation.getWorld()))) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(baseLocation)
                    .copyEntities(false)
                    .build();
            Operations.complete(operation);
        } catch (Exception ex) {

        }
        int diffX = region.getPos2().getX() - region.getPos1().getX();
        int diffY = region.getPos2().getY() - region.getPos1().getY();
        int diffZ = region.getPos2().getZ() - region.getPos1().getZ();
        CuboidTest walls = new CuboidTest(BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation), BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation.add(diffX, diffY, diffZ)));
        for (int i = 0; i < 4; i++) {
            CuboidTest cuboidTest = walls.getWalls()[i];
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
    }

    public void paste(int gridBlocks) {
        BlockVector3 pos1Vector = BlockVector3.at(cuboid.getPoint1().getBlockX(), cuboid.getPoint1().getY(), cuboid.getPoint1().getZ());
        BlockVector3 pos2Vector = BlockVector3.at(cuboid.getPoint2().getBlockX(), cuboid.getPoint2().getY(), cuboid.getPoint2().getZ());
        CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.adapt(spawnLocation.getWorld()), pos1Vector, pos2Vector);
        //Bukkit.broadcastMessage("blocks " + cuboidRegion.getWidth() * cuboidRegion.getLength() * cuboid.getHeight());
        Clipboard clipboard = fromCuboid(cuboidRegion);
        //clipboard.setOrigin(BlockVector3.at(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ()));
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(new AffineTransform().rotateY(rotation.getRotationTheta())));
        //clipboard.paste(BukkitAdapter.adapt(spawnLocation.getWorld()), cuboidRegion.getMinimumPoint());
        final Location location = rotationCorner(spawnLocation, rotation, gridBlocks);
        BlockVector3 baseLocation = BlockVector3.at(location.getX(), cuboidRegion.getMaximumY(), location.getZ());
        EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(spawnLocation.getWorld()));
        Operation operation = clipboardHolder.createPaste(editSession).copyEntities(false).to(baseLocation).build();
        Operations.complete(operation);
        Bukkit.broadcastMessage(editSession.getBlockChangeCount() + " ");
        Bukkit.broadcastMessage(betterLoc(BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation)));

        /*Cuboid walls = new Cuboid(BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation),
                BukkitAdapter.adapt(spawnLocation.getWorld(), baseLocation).add(gridBlocks - 1, -cuboid.getHeight(), gridBlocks - 1));
        for (int i = 0; i < 4; i++) {
            if (doors[i] == 1) {
                walls.cuboidEdges(i).stream().filter(block -> block.getType() == Material.LIME_WOOL)
                        .forEach(door -> {
                            door.setType(Material.AIR);
                        });
            }
        }*/
        //Bukkit.broadcastMessage("Pasted");

        /*for(int i=0; i<blockList.size(); i++) {
            Block sourceBlock = it.next();
            int x = (int) (sourceBlock.getLocation().getBlockX() - tempDungeonBuilds.getLocation1().getX() + spawnLocation.getBlockX()),
                    y = sourceBlock.getLocation().getBlockY(),
                    z = (int) (sourceBlock.getLocation().getBlockZ() - tempDungeonBuilds.getLocation1().getZ() + spawnLocation.getBlockZ());

            final Location newLoc = new Location(spawnLocation.getWorld(), x, y, z);
            Block targetBlock = spawnLocation.getWorld().getBlockAt(newLoc.add(0, 0, 0));
            targetBlock.setBlockData(sourceBlock.getBlockData(), false);
        }*/
    }

    public static Clipboard fromCuboid(CuboidRegion region) {
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        ForwardExtentCopy copy = new ForwardExtentCopy(Objects.requireNonNull(region.getWorld()), region, clipboard, region.getMinimumPoint());
        copy.setCopyingEntities(false);
        Operations.complete(copy);
        return clipboard;
    }

    @Override
    public void run() {

        for(int i=0; i<blocksPerTick; i++) {
            if(!it.hasNext()){
                Bukkit.getScheduler().cancelTask(taskID);
                break;
            }
            //location player - location block li kayn tma
            Block sourceBlock = it.next();
            int x = (int) (sourceBlock.getLocation().getBlockX() - tempDungeonBuilds.getLocation1().getX() + spawnLocation.getBlockX()),
                    y = sourceBlock.getLocation().getBlockY(),
                    z = (int) (sourceBlock.getLocation().getBlockZ() - tempDungeonBuilds.getLocation1().getZ() + spawnLocation.getBlockZ());
            /*int x = spawnLocation.getBlockX() - sourceBlock.getLocation().getBlockX(),
                    y = sourceBlock.getLocation().getBlockY(),
                    z = spawnLocation.getBlockZ() - sourceBlock.getLocation().getBlockZ();*/
            final Location newLoc = new Location(spawnLocation.getWorld(), x, y, z);
            //Location rotatedLocation = rotateLocation(sourceBlock.getLocation(), rotation);
            Block targetBlock = spawnLocation.getWorld().getBlockAt(newLoc.add(0, 0, 0));
            targetBlock.setBlockData(sourceBlock.getBlockData(), false);
        }
    }

    /*public Vector rotateX(Vector inputVector, double thetaInRadians) {
        Vector v1 = new Vector(1,0,0).multiply(inputVector.getX());
        Vector v2 = new Vector(0,Math.cos(thetaInRadians),Math.sin(thetaInRadians)).multiply(inputVector.getY());
        Vector v3 = new Vector(0,-1*Math.sin(thetaInRadians),Math.cos(thetaInRadians)).multiply(inputVector.getZ());
        Vector resultVector = v1.add(v2).add(v3);
        return resultVector;
    }

    public Vector rotateZ(Vector inputVector, double thetaInRadians) {
        Vector v1 = new Vector(Math.cos(thetaInRadians),-Math.sin(thetaInRadians),0).multiply(inputVector.getX());
        Vector v2 = new Vector(Math.sin(thetaInRadians),Math.cos(thetaInRadians), 0).multiply(inputVector.getY());
        Vector v3 = new Vector(0,0, 1).multiply(inputVector.getZ());
        Vector resultVector = v1.add(v2).add(v3);
        return resultVector;
    }*/

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public int getTaskID() {
        return taskID;
    }

    public RoomRotation getRotation() {
        return rotation;
    }
}
