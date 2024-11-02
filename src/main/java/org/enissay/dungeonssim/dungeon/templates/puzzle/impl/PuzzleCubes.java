package org.enissay.dungeonssim.dungeon.templates.puzzle.impl;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.minecraft.world.level.block.TargetBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.CuboidTest;
import org.enissay.dungeonssim.dungeon.EventManager;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.templates.MonsterFrequency;
import org.enissay.dungeonssim.dungeon.templates.puzzle.IPuzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleType;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.entities.hostile.skeleton.UndeadSkeleton;
import org.enissay.dungeonssim.entities.hostile.spiders.Tarantula;
import org.enissay.dungeonssim.entities.hostile.undeads.Undead;
import org.enissay.dungeonssim.entities.hostile.undeads.UndeadFrozen;
import org.enissay.dungeonssim.entities.hostile.undeads.UndeadKnight;
import org.enissay.dungeonssim.utils.Cuboid;
import org.enissay.dungeonssim.utils.LuckUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PuzzleCubes implements IPuzzle {

    private final List<Block> blocksLocation = new ArrayList<>();
    private final Random random = new Random();
    private Map<Block, List<Block>> blockRelations = new HashMap<>();

    @Override
    public PuzzleType getType() {
        return PuzzleType.CUBE_ROTATION;
    }

    private boolean allPumpkinsFacingNorth() {
        for (Block block : blocksLocation) {
            BlockData blockData = block.getBlockData();
            if (block.getType() == Material.CARVED_PUMPKIN && blockData instanceof Directional) {
                if (((Directional) blockData).getFacing() != BlockFace.NORTH) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getPumpkinsFacingNorth() {
        int i = 0;
        for (Block block : blocksLocation) {
            BlockData blockData = block.getBlockData();
            if (block.getType() == Material.CARVED_PUMPKIN && blockData instanceof Directional) {
                if (((Directional) blockData).getFacing() == BlockFace.NORTH) {
                    i++;
                }
            }
        }
        return i;
    }

    private void updateAdjacentPumpkins(Location location) {
        Block clickedBlock = location.getBlock();
        List<Block> randomBlocks = blockRelations.get(clickedBlock);
        if (randomBlocks == null) {
            randomBlocks = getRandomBlocks(clickedBlock, 2);
            if (!randomBlocks.contains(clickedBlock)) randomBlocks.add(clickedBlock);
            blockRelations.put(clickedBlock, randomBlocks);
        }

        for (Block block : randomBlocks) {
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Directional) {
                rotateBlock(block);
            }
        }
    }

    private void rotateBlock(Block block) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional) {
            updatePumpkinFacing(block);
        }
    }

    private void updatePumpkinFacing(Block block) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional) {
            Directional directional = (Directional) blockData;
            directional.setFacing(getNextFace(directional.getFacing()));
            block.setBlockData(blockData);
        }
    }

    private BlockFace getNextFace(BlockFace currentFace) {
        BlockFace[] facesOrder = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        int currentIndex = 0;
        for (int i = 0; i < facesOrder.length; i++) {
            if (facesOrder[i] == currentFace) {
                currentIndex = i;
                break;
            }
        }
        int nextIndex = (currentIndex + 1) % facesOrder.length;
        return facesOrder[nextIndex];
    }
    private List<Block> getRandomBlocks(Block original, int count) {
        List<Block> randomBlocks = new ArrayList<>();
        List<Block> copy = new ArrayList<>(blocksLocation);
        if (copy.contains(original)) copy.remove(original);
        for (int i = 0; i < Math.min(count, blocksLocation.size()); i++) {
            int index = random.nextInt(copy.size());
            randomBlocks.add(copy.remove(index));
        }

        return randomBlocks;
    }

    @Override
    public void initEvents(DungeonRoom room) {
        /**
         * ADD HOLOGRAM FOR INSTRUCTIONS
         */
        if (room.getLocationFromTemplate("holo_1") != null) {
            final Location hologramLoc = room.getLocationFromTemplate("holo_1");
            final Hologram holo = DHAPI.createHologram("holo_1_dungeon_" + room.getDungeon().getID(), hologramLoc, false);
            DHAPI.addHologramLine(holo, "&d&lCUBE ROTATION");
            DHAPI.addHologramLine(holo, "");
            DHAPI.addHologramLine(holo, "&eTry to face all the pumpkins");
            DHAPI.addHologramLine(holo, "&etowards you by &6&lRIGHT CLICKING&e to");
            DHAPI.addHologramLine(holo, "&eopen the door to the right.");
            DHAPI.addHologramLine(holo, "");
            DHAPI.addHologramLine(holo, "");
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (holo != null)
                        DHAPI.setHologramLine(holo, 6, "&aPROGRESS: &d" + getPumpkinsFacingNorth() + "&7/&5" + blocksLocation.size());
                    if (room.getPuzzle() != null && room.getPuzzle().isCompleted()) {
                        if (holo != null)
                            DHAPI.setHologramLine(holo, 6, "&aPROGRESS: &2COMPLETED");
                        cancel();
                    }
                }
            }.runTaskTimer(DungeonsSim.getInstance(), 0, 20);

            room.getDungeon().addHologram(holo);
            /*TextHologram hologram = new TextHologram("test")
                    //.setText("aaaaa\nfdfdfdf")
                    .setMiniMessageText("<red>whaaat")
                    .setSeeThroughBlocks(false)
                    .setBillboard(Display.Billboard.VERTICAL)
                    .setShadow(true)
                    .setScale(1.5F,1.5F,1.5F)
                    .setTextOpacity((byte) 200)
                    .setBackgroundColor(Color.fromARGB(20, 255, 236, 222).asARGB());
            room.getDungeon().addHologram(hologram, hologramLoc);*/
        }

        //SETUP RANDOM FACES
        if (room.getLocationFromTemplate("puzzleStart") != null) {
            final Random random = new Random();

            for (int i = 0; i < 5; i++) {
                final Location puzzleStart = room.getLocationFromTemplate("puzzleStart");
                final Location newLoc = puzzleStart.add(-i, 0, 0);
                BlockFace[] horizontalFaces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
                BlockFace randomHorizontalFace = horizontalFaces[random.nextInt(horizontalFaces.length)];
                final Block block = newLoc.getBlock();
                block.setType(Material.CARVED_PUMPKIN);
                BlockData blockData = block.getBlockData();
                if (blockData instanceof Directional) {
                    ((Directional) blockData).setFacing(randomHorizontalFace);
                    block.setBlockData(blockData);
                }
                blocksLocation.add(block);
            }
        }
        //ON CLICK
        EventManager.on(PlayerInteractEvent.class, event -> {
            final Puzzle puzzle = room.getPuzzle();
            if (puzzle != null) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK &&
                        room.getCuboid().isIn(event.getClickedBlock().getLocation()) &&
                        blocksLocation.contains(event.getClickedBlock()) &&
                        !room.getDungeon().isPuzzleCompleted(puzzle)) {
                    Block block = event.getClickedBlock();
                    BlockData blockData = block.getBlockData();
                    if (blockData instanceof Directional) {
                        //updateAdjacentPumpkins(block.getLocation());
                        rotateBlock(block);
                        if (allPumpkinsFacingNorth()) {
                            puzzle.complete(event.getPlayer());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCompletion(DungeonRoom room) {
        //blocksLocation.forEach(block -> block.setType(Material.JACK_O_LANTERN));
        new BukkitRunnable() {
            int start = 0;
            int end = blocksLocation.size() - 1;
            @Override
            public void run() {
                blocksLocation.get(start).setType(Material.JACK_O_LANTERN);
                room.getWatchers().forEach(uuid -> {
                    final Player player = Bukkit.getPlayer(uuid);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2f,
                            start == 0 ? 0.5f : start == 1 ? 0.7f : start > 1 ? 1f : 1.2f);
                    //player.sendMessage((start == 0 ? 0.5f : start == 1 ? 0.7f : start > 1 ? 1f : 1.2f) + "");
                });
                start++;
                blocksLocation.get(end).setType(Material.JACK_O_LANTERN);
                end--;

                if (blocksLocation.stream()
                        .filter(block -> block.getType() == Material.JACK_O_LANTERN).count() >= blocksLocation.size()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, 20);

        /**
         * OPEN DOOR
         */
        final Location location1 = room.getLocationFromTemplate("doorLoc1");
        final Location location2 = room.getLocationFromTemplate("doorLoc2");

        if (location1 != null && location2 != null) {
            final CuboidTest cuboidtest = new CuboidTest(location1, location2);
            final Cuboid cuboid = new Cuboid(location1, location2);
            final Material material = Material.IRON_BARS;
            new BukkitRunnable() {
                //int start = 0;
                int currentY = cuboidtest.getLowerY();
                @Override
                public void run() {
                    cuboid.blocksListed().forEach(block -> {
                        if (block.getY() == currentY && block.getType() == material) block.setType(Material.AIR);
                    });
                    location1.getWorld().playSound(cuboidtest.getCenter(), Sound.BLOCK_ANVIL_LAND, 2f,2f);
                    /*room.getWatchers().forEach(uuid -> {
                        final Player player = Bukkit.getPlayer(uuid);
                        player.playSound(cuboidtest.getCenter(), Sound.BLOCK_ANVIL_LAND, 2f,2f);
                    });*/
                    if (cuboid.blocksListed().stream()
                            .filter(block -> block.getType() == material).count() == 0 ||
                            currentY >= cuboidtest.getUpperY()) {
                        this.cancel();
                    }
                    currentY++;
                }
            }.runTaskTimer(DungeonsSim.getInstance(), 20, 7);
            /*for (int y = cuboidtest.getLowerY(); y <= cuboidtest.getUpperY(); y++) {
                cuboid.blocksListed().forEach(block -> {

                });
            }*/
        }

        new BukkitRunnable() {
            int start = 0;
            @Override
            public void run() {
                blocksLocation.get(start).setType(Material.BEDROCK);
                blocksLocation.get(start).getWorld().playEffect(blocksLocation.get(start).getLocation(), Effect.STEP_SOUND, Material.BEDROCK);
                room.getWatchers().forEach(uuid -> {
                    final Player player = Bukkit.getPlayer(uuid);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 2f,0.8f);
                });
                start++;

                if (blocksLocation.stream()
                        .filter(block -> block.getType() == Material.BEDROCK).count() >= blocksLocation.size()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 20 * 5, 10);

        //spawn mobs on mobsLocation
        MonsterFrequency monsterFrequency = new MonsterFrequency();

        monsterFrequency.addFrequency(UndeadSkeleton.class, room.getTemplate(), room.getRoomName(), 20.0);
        monsterFrequency.addFrequency(Tarantula.class, room.getTemplate(), room.getRoomName(), 30.0);
        monsterFrequency.addFrequency(Undead.class,room.getTemplate(), room.getRoomName(), 35.0);

        Dungeon dungeon = room.getDungeon();

        int spawnedMobs = 0;
        int capMobs = 10;
        Location centerLoc = room.getCuboid().getCenter();
        int minY = 84;
        int maxY = (int)(minY + (room.getCuboid().getHeight() / 1.65));
        Map<Class<? extends CustomMob>, Double> randomMap = new HashMap<>();
        monsterFrequency.getSpawnInfosForRoom(room.getRoomName()).forEach(spawnInfo -> {
            randomMap.put(monsterFrequency.getMobFromSpawnInfo(spawnInfo), spawnInfo.getChance());
        });

        while (spawnedMobs < capMobs) {
            int randY = minY + (int) (Math.random() * ((maxY - minY) + 1));
            //centerLoc.setY(76);
            double offsetX = (Math.random() - 0.5) * room.getCuboid().getXWidth() / 2;
            double offsetZ = (Math.random() - 0.5) * room.getCuboid().getZWidth() / 2;

            Location spawnLoc = centerLoc.clone().add(offsetX, 0, offsetZ);
            spawnLoc.setY(randY);
            if (isSpawnable(spawnLoc) &&
                    spawnLoc.distance(centerLoc) <= 15) {
                Class<? extends CustomMob> selectedMonsterClass = LuckUtil.getRandomWeighted(randomMap);

                if (selectedMonsterClass != null) {
                    //Bukkit.broadcastMessage("chosen " + selectedMonsterClass.getName());

                    try {
                        Constructor<? extends CustomMob> constructor = selectedMonsterClass.getConstructor(Location.class, int.class, double.class);
                        CustomMob mob = constructor.newInstance(spawnLoc, dungeon.getDungeonDifficulty().getLevel(), (1.8*(dungeon.getPlayers().size()-1)));
                        dungeon.addCustomMob(mob.getNMSEntity());

                        spawnedMobs++;
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                             InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }else {
                    Bukkit.broadcastMessage("error spawning mobs ");
                    break;
                }
            }
        }
    }

    private boolean isSpawnable(Location loc) {
        Block feetBlock = loc.getBlock(), headBlock = loc.clone().add(0, 1, 0).getBlock(), upperBlock = loc.clone().add(0, 2, 0).getBlock();
        return feetBlock.isPassable() && !feetBlock.isLiquid() && headBlock.isPassable() && !headBlock.isLiquid() && upperBlock.isPassable() && !upperBlock.isLiquid();
    }
}
