package org.enissay.dungeonssim.dungeon.system;

import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.CuboidTest;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.templates.RoomLocation;
import org.enissay.dungeonssim.dungeon.templates.RoomRotation;
import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleTemplate;
import org.enissay.dungeonssim.dungeon.templates.puzzle.PuzzleType;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PuzzleHandler;
import org.enissay.dungeonssim.utils.Cuboid;
import org.enissay.dungeonssim.utils.FileUtils;
import org.enissay.dungeonssim.utils.LuckUtil;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DungeonParser {

    private static Map<TempDungeonBuilds, BlockArrayClipboard> clipboards = new HashMap<>();
    public static void paste(Location location, File file) {

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        Clipboard clipboard;

        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {

                if (location.getWorld() == null)
                    throw new NullPointerException("Failed to paste schematic due to world being null");

                com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());

                EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

                clipboard = clipboardReader.read();

                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(blockVector3)
                        .ignoreAirBlocks(true)
                        .build();

                try {
                    Operations.complete(operation);
                    //editSession.close();
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<TempDungeonBuilds, BlockArrayClipboard> getClipboards() {
        return clipboards;
    }

    public static BlockArrayClipboard getClipboard(final TempDungeonBuilds room) {
        return clipboards.get(clipboards.keySet().stream().filter(tempDungeonBuilds -> tempDungeonBuilds.getTemplateName().equals(room.getTemplateName()) && tempDungeonBuilds.getName().equals(room.getName())).findAny().orElse(null));
    }

    public static Clipboard loadRoomSchematic(final TempDungeonBuilds room) {
        Clipboard clipboard;

        final File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/schematics/" + room.getTemplateName() + "/" + room.getName() + ".schem");

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return clipboard;
    }
    public static void registerClipboardForRooms() {
        DungeonHandler.getTemplateList().forEach(dungeonTemplate -> {
            DungeonHandler.loadRooms(dungeonTemplate.getName()).forEach(dungeonRoom -> {
                final RoomLocation roomLoc1 = dungeonRoom.getLocation1();
                final RoomLocation roomLoc2 = dungeonRoom.getLocation2();
                final World world = Bukkit.getWorld("world");
                final Location loc1 = new Location(world, roomLoc1.getX(), roomLoc1.getY(), roomLoc1.getZ());
                final Location loc2 = new Location(world, roomLoc2.getX(), roomLoc2.getY(), roomLoc2.getZ());
                final Cuboid cuboid = new Cuboid(loc1, loc2);
                /*BlockVector3 pos1Vector = BlockVector3.at(cuboid.getPoint1().getBlockX(), cuboid.getPoint1().getY(), cuboid.getPoint1().getZ());
                BlockVector3 pos2Vector = BlockVector3.at(cuboid.getPoint2().getBlockX(), cuboid.getPoint2().getY(), cuboid.getPoint2().getZ());
                CuboidRegion region = new CuboidRegion(pos1Vector, pos2Vector);
                BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();*/

                /*//scheduler.runTaskAsynchronously(DungeonsSim.getInstance(), () -> {
                    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                            BukkitAdapter.adapt(Bukkit.getWorld("world")), region, clipboard, region.getMinimumPoint()
                    );
                    Operations.complete(forwardExtentCopy);
                //});

                clipboards.put(dungeonRoom, clipboard);*/

                final File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                        "/schematics/" + dungeonTemplate.getName() + "/" + dungeonRoom.getName() + ".schem");
                if (!file.exists()) {
                    try {
                        FileUtils.createFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    BlockVector3 pos1Vector = BlockVector3.at(cuboid.getPoint1().getBlockX(), cuboid.getPoint1().getY(), cuboid.getPoint1().getZ());
                    BlockVector3 pos2Vector = BlockVector3.at(cuboid.getPoint2().getBlockX(), cuboid.getPoint2().getY(), cuboid.getPoint2().getZ());
                    CuboidRegion region = new CuboidRegion(pos1Vector, pos2Vector);
                    BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

                    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                            BukkitAdapter.adapt(Bukkit.getWorld("world")), region, clipboard, region.getMinimumPoint()
                    );
                    try {
                        Operations.complete(forwardExtentCopy);
                    } catch (WorldEditException e) {
                        throw new RuntimeException(e);
                    }

                    try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                        writer.write(clipboard);
                        System.out.println("Registered schematic: " + dungeonTemplate.getName() + "/" + dungeonRoom.getName() + ".schematic");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }

    public static void changePhysicalDoorState(final DungeonRoom dungeonRoom, final Material mask, final Material material, final int doorIndex) {
        final Cuboid cuboid = dungeonRoom.getCuboid();
        CuboidTest newCuboid = new CuboidTest(cuboid.getPoint1(), cuboid.getPoint2());
        CuboidTest cuboidTest = newCuboid.getWalls()[doorIndex];
        cuboidTest.forEach(loc -> {
            final Block block = loc.getBlock();
            if (block.getType() == mask) {
                block.setType(material);
            }
        });

    }

    public static void openAllKeyDoors(final DungeonRoom dungeonRoom) {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "LEFT");
        map.put(1, "RIGHT");
        map.put(2, "DOWN");
        map.put(3, "UP");

        for (int i = 0; i < dungeonRoom.getGridCell().getDoors().length; i++) {
            if (dungeonRoom.getGridCell().getDoors()[i] == 2) {
                if (getOpposite(dungeonRoom.getDungeon().getDungeonGeneration(), i, dungeonRoom.getGridCell()) != null) {
                    final DungeonRoom opposite = DungeonHandler.getRoomFromCell(getOpposite(dungeonRoom.getDungeon().getDungeonGeneration(), i, dungeonRoom.getGridCell()));

                    List<DungeonGeneration.GridCell> keyRooms = dungeonRoom.getDungeon().getDungeonGeneration().getKeyRooms();
                    List<DungeonRoom> openedRooms = dungeonRoom.getDungeon().getRoomsOpened();

                    if ((!dungeonRoom.getDungeon().getDungeonGeneration().getKeyRooms().contains(opposite.getGridCell()) &&
                            !dungeonRoom.getDungeon().getRoomsOpened().contains(opposite)) || !(dungeonRoom.getDungeon().getRoomsOpened().contains(opposite)) && dungeonRoom.getDungeon().getRoomsOpened().contains(dungeonRoom) &&
                            dungeonRoom.getDungeon().getDungeonGeneration().getKeyRooms().contains(opposite.getGridCell()) && dungeonRoom.getDungeon().getDungeonGeneration().getKeyRooms().contains(dungeonRoom.getGridCell())) {

                        changePhysicalDoorState(dungeonRoom, Material.BLACK_WOOL, Material.AIR, i);
                        changePhysicalDoorState(opposite, Material.BLACK_WOOL, Material.AIR, getOppositeDirection(i));
                    }else if (!openedRooms.contains(dungeonRoom) &&
                            openedRooms.contains(opposite) && keyRooms.contains(dungeonRoom.getGridCell()) &&
                            keyRooms.contains(opposite.getGridCell())) {

                        changePhysicalDoorState(dungeonRoom, Material.BLACK_WOOL, Material.AIR, i);
                        changePhysicalDoorState(opposite, Material.BLACK_WOOL, Material.AIR, getOppositeDirection(i));
                    }
                }
            }
        }
    }

    // Method to select a template considering maximum room count
    private static DungeonTemplate selectTemplate(DungeonGeneration generation, DungeonGeneration.GridCell gridCell, Map<DungeonTemplate, Double> templates, Map<DungeonGeneration.GridCell, DungeonTemplate> roomTemp) {
        DungeonTemplate selectedTemplate = LuckUtil.getRandomWeighted(templates, generation.getRandom());
        //DungeonTemplate originalTemplate = selectedTemplate;
        DungeonTemplate finalSelectedTemplate = selectedTemplate;
        long templateCount = roomTemp.values().stream().filter(temp -> temp == finalSelectedTemplate).count();

        // If the maximum room count for the selected template is reached, choose another template
        while (selectedTemplate.getMaxRooms() > 0 && templateCount == selectedTemplate.getMaxRooms()) {
            Map<DungeonTemplate, Double> alternativeTemplates = getAlternativeTemplates(selectedTemplate, templates);
            selectedTemplate = LuckUtil.getRandomWeighted(alternativeTemplates, generation.getRandom());

            DungeonTemplate finalSelectedTemplate1 = selectedTemplate;
            templateCount = roomTemp.values().stream().filter(temp -> temp == finalSelectedTemplate1).count();
        }

        roomTemp.put(gridCell, selectedTemplate);
        return selectedTemplate;
    }

    // Method to get alternative templates excluding the current template
    private static Map<DungeonTemplate, Double> getAlternativeTemplates(DungeonTemplate currentTemplate, Map<DungeonTemplate, Double> allTemplates) {
        Map<DungeonTemplate, Double> alternativeTemplates = new HashMap<>();
        List<DungeonTemplate> shuffledTemplates = new ArrayList<>(allTemplates.keySet());
        Collections.shuffle(shuffledTemplates);

        shuffledTemplates.forEach(template -> {
            if (!template.getName().equals("SPAWN_ROOM") && !template.getName().equals("BOSS_ROOM") && !template.getName().equals(currentTemplate.getName())) {
                alternativeTemplates.put(template, allTemplates.get(template));
            }
        });
        return alternativeTemplates;
    }

    public static void parse(final Dungeon dungeon, final DungeonGeneration dungeonGeneration, final Location location) {
        if (dungeon == null || dungeonGeneration == null || location == null) return;

        LinkedList<DungeonGeneration.GridCell> visited = new LinkedList<>();
        LinkedList<DungeonGeneration.GridCell> pastedRooms = new LinkedList<>();
        HashMap<DungeonGeneration.GridCell, String> roomNames = new HashMap<>();
        HashMap<DungeonGeneration.GridCell, DungeonTemplate> roomTemp = new HashMap<>();
        HashMap<DungeonTemplate, String> tempRooms = new HashMap<>();

        Map<DungeonTemplate, Double> templates = new HashMap<>();

        DungeonHandler.getTemplateList().forEach(temp -> {
            if (!temp.getName().equals("SPAWN_ROOM") && !temp.getName().equals("BOSS_ROOM"))
                templates.put(temp, (double) ((1.0 / DungeonHandler.getTemplateList().size()) * 100));
        });

        Map<Integer, String> map = new HashMap<>();
        map.put(0, "LEFT");
        map.put(1, "RIGHT");
        map.put(2, "DOWN");
        map.put(3, "UP");
        /**
         * FIRST ITERATION
         */
        dungeonGeneration.getRooms().forEach(gridCell -> {
            int input = dungeonGeneration.getGridMap()[gridCell.getX()][gridCell.getY()];
            DungeonTemplate template = DungeonHandler.getTemplateFromID(input);

            int left = 0;
            int right = 0;
            int down = 0;
            int up = 0;

            int x = gridCell.getX();
            int y = gridCell.getY();

            if (x < dungeonGeneration.getGridWidth() - 1 && dungeonGeneration.getGridMap()[x + 1][y] != 0) {
                right = 1;
            }
            if (x > 0 && x <= dungeonGeneration.getGridWidth() - 1 && dungeonGeneration.getGridMap()[x - 1][y] != 0) {
                left = 1;

            }
            if (y < dungeonGeneration.getGridHeight() - 1 && dungeonGeneration.getGridMap()[x][y + 1] != 0) {
                down = 1;
            }
            if (y > 0 && y <= dungeonGeneration.getGridHeight() - 1 && dungeonGeneration.getGridMap()[x][y - 1] != 0) {
                up = 1;
            }
            int[] doors = new int[]{left, right, down, up};

            gridCell.setDoors(doors);

            if (!template.getName().equals("SPAWN_ROOM") && !template.getName().equals("BOSS_ROOM")) {
                template = selectTemplate(dungeonGeneration, gridCell, templates, roomTemp);
            }

            TempDungeonBuilds tempDungeonBuilds;
            String chosenRoom = LuckUtil.getRandomWeighted(template.getRoomsFromTemplate(), dungeonGeneration.getRandom());
            if (gridCell != null && gridCell.getDoors() != null) {
                if (template.hasUniqueRooms() && template.getBuilds().keySet().size() > 1 &&
                        tempRooms.get(template) != null && tempRooms.containsKey(template) && tempRooms.get(template).equalsIgnoreCase(chosenRoom)) {
                    final Map<String, Double> newRooms = new HashMap<>();
                    String finalChosenRoom = chosenRoom;
                    template.getRoomsFromTemplate().forEach((roomName, chance) -> {
                        if (!roomName.equalsIgnoreCase(finalChosenRoom)) newRooms.put(roomName, chance);
                    });
                    chosenRoom = LuckUtil.getRandomWeighted(newRooms, dungeonGeneration.getRandom());
                }
                for (int j = 0; j < gridCell.getDoors().length; j++) {
                    final DungeonGeneration.GridCell opposites = getOpposite(dungeonGeneration, j, gridCell);
                    if (chosenRoom.contains("RARE")) {
                        //Bukkit.broadcastMessage("Spawned rare room " + x + " " + y);
                        if (opposites != null) {
                            //Bukkit.broadcastMessage(map.get(j) + " " + DungeonHandler.getTemplateFromID(dungeonGeneration.getGridMap()[opposites.getX()][opposites.getY()]).getName());
                        }
                    }
                    if (opposites != null && DungeonHandler.getTemplateFromID(dungeonGeneration.getGridMap()[opposites.getX()][opposites.getY()]).getName().contains("SPAWN_ROOM") &&
                            chosenRoom.contains("RARE")) {
                        chosenRoom = template.getRoomsFromTemplate().keySet().stream().toList().get(0);
                    }
                }
            }
            tempRooms.put(template, chosenRoom);

            roomNames.put(gridCell, chosenRoom);
            tempDungeonBuilds = DungeonHandler.loadRoom(template.getName(), chosenRoom);

            if (tempDungeonBuilds.getTemplateName().equals("BOSS_ROOM")) dungeonGeneration.getKeyRooms().add(gridCell);
            else if (tempDungeonBuilds.getTemplateName().equals("NORMAL_ROOM") && chosenRoom.contains("RARE")) dungeonGeneration.getKeyRooms().add(gridCell);

            Map<Integer, Double> doorChance = new HashMap<>();
            doorChance.put(0, 99.0);
            doorChance.put(1, 1.0);

            /**
             * REMEMBER TO PREVENT THIS FROM HAPPENING IF ITS NEAR BOSS ROOM
             * AND PREVENT FROM ROOM_RARE TO SPAWN NEAR SPAWN_ROOM
             * REMEMBER TO FIX 2x2 ROOMS PROBLEMS IF 2X2 EXAMPLE:
             * IF 4x4 AND NO ROOM LEFT THEN CANCEL
             *
             */
            if (input == 1 && !roomNames.get(gridCell).equals("ROOM_RARE")) {
                if (dungeonGeneration.getAvailableDoors(gridCell).length > 2) {
                    for (int i = 0; i < 4 - dungeonGeneration.getAvailableDoors(gridCell).length; i++) {
                        int random = dungeonGeneration.randomDoor(gridCell);
                        final DungeonGeneration.GridCell opposite = getOpposite(dungeonGeneration, random, gridCell);
                        final DungeonGeneration.GridCell neighbor = dungeonGeneration.getRoom(x + 1, y - 1);

                        //Try to prevent rare from spawning near boss
                        boolean canPass = true;
                        for (int j = 0; j < gridCell.getDoors().length; j++) {
                            final DungeonGeneration.GridCell opposites = getOpposite(dungeonGeneration, j, gridCell);
                            if (opposites != null && dungeonGeneration.getGridMap()[opposites.getX()][opposites.getY()] == 2) canPass = false;
                        }
                    }
                }
            }
            else if (input == 1 && roomNames.get(gridCell).equals("ROOM_RARE")) {
                //boolean canPass = true;
                for (int availableDoor : dungeonGeneration.getAvailableDoors(gridCell)) {

                    gridCell.getDoors()[availableDoor] = 2;
                    closeOpposite(dungeonGeneration, 2, availableDoor, gridCell);
                }
            }
            else if (input == 2) {
                for (int availableDoor : dungeonGeneration.getAvailableDoors(gridCell)) {

                    gridCell.getDoors()[availableDoor] = 2;
                    closeOpposite(dungeonGeneration, 2, availableDoor, gridCell);
                }
                //Leave only one key door
                for (int i = 0; i < dungeonGeneration.getAvailableDoors(gridCell).length; i++) {
                    int random = dungeonGeneration.randomDoor(gridCell);
                    gridCell.getDoors()[random] = 0;//0 TO CLOSE NORMAL
                    closeOpposite(dungeonGeneration, 0, random, gridCell);
                }
            }
        });


        dungeonGeneration.getRooms().forEach(room -> {
            if (room.getDoors() != null) {
                for (int i = 0; i < room.getDoors().length; i++) {
                    if (room.getDoors()[i] != 1)
                        closeOpposite(dungeonGeneration, room.getDoors()[i], i, room);
                }
            }
        });

        /**
         * SECOND ITERATION
         */

        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .checkMemory(true)
                //.forceWNA()
                .allowedRegionsEverywhere()
                .world(BukkitAdapter.adapt(Bukkit.getWorld("world")))
                //.limitUnlimited()
                .relightMode(RelightMode.ALL)
                //.maxBlocks(1)
                .build();
        dungeonGeneration.getRooms().forEach((gridCell) -> {

            int input = dungeonGeneration.getGridMap()[gridCell.getX()][gridCell.getY()];
            DungeonTemplate template = DungeonHandler.getTemplateFromID(input);
            if (!template.getName().equals("SPAWN_ROOM") && !template.getName().equals("BOSS_ROOM")) {
                template = roomTemp.get(gridCell);
            }
            int x = gridCell.getX();
            int y = gridCell.getY();

            RoomRotation initialDirection = RoomRotation.SOUTH;//RoomRotation.values()[new Random().nextInt(RoomRotation.values().length)];

            int startX = /*(DungeonHandler.getDungeons().size() + dungeonGeneration.getGridWidth() * dungeonGeneration.getGridBlocks()) +*/ (location.getBlockX() + x * dungeonGeneration.getGridBlocks()) - (dungeonGeneration.getGridBlocks() * dungeonGeneration.getGridWidth()/2);
            int startY = (location.getBlockZ() + y * dungeonGeneration.getGridBlocks()) - (dungeonGeneration.getGridBlocks() * dungeonGeneration.getGridHeight()/2);

            TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(template.getName(), roomNames.get(gridCell));

            RoomPasting roomPastingSpawn = new RoomPasting(tempDungeonBuilds, new Location(location.getWorld(), startX, location.getBlockY(), startY), initialDirection,
                    gridCell.getDoors(), editSession);//LEFT, RIGHT, DOWN, UP
            final Cuboid cuboid = roomPastingSpawn.paste(dungeon, dungeonGeneration.getGridBlocks());
            dungeon.addRoom(new DungeonRoom(dungeon, template, roomNames.get(gridCell), cuboid, gridCell, dungeon.getRooms().size() + 1));
            pastedRooms.add(gridCell);
        });

        dungeon.getRooms().forEach(room -> {
            PuzzleType type = null;
            if (room.getTemplate() instanceof PuzzleTemplate) {
                PuzzleTemplate puzzleTemplate = (PuzzleTemplate) room.getTemplate();
                if (puzzleTemplate.getPuzzles().containsKey(room.getRoomName()))
                    type = puzzleTemplate.getPuzzles().get(room.getRoomName());
                room.addPuzzle((room.getLocationFromTemplate("puzzleStart") != null ? room.getLocationFromTemplate("puzzleStart") : null), type);
                //PuzzleHandler.addPuzzle((room.getLocationFromTemplate("puzzleStart") != null ? room.getLocationFromTemplate("puzzleStart") : null), room, type);
            }
        });

        dungeon.getRooms().forEach(room -> {
            final Puzzle puzzle = room.getPuzzle();
            if (puzzle != null && puzzle.getPuzzleType() != null &&
                    PuzzleHandler.getPuzzleTemplate(puzzle.getPuzzleType()) != null) {
                PuzzleHandler.getPuzzleTemplate(puzzle.getPuzzleType()).initEvents(room);
            }
        });

        PuzzleHandler.getPuzzles(dungeon).forEach(puzzle -> {
            puzzle.whenComplete((completer, puzz) -> {
                MessageUtils.broadcast("&b" + completer.getName() + "&e has completed the puzzle &d" + puzz.getPuzzleType().getName(),
                        MessageUtils.BroadcastType.MESSAGE, MessageUtils.TargetType.DUNGEON, dungeon);
            });
        });
        DungeonHandler.addDungeon(dungeon);
    }


    public static DungeonGeneration.GridCell getOpposite(DungeonGeneration dungeonGeneration, int doorIndex, DungeonGeneration.GridCell gridCell) {
        //2 DOWN = y + 1 UP 3
        //1 RIGHT = x + 1 LEFT 0
        //0 LEFT = x - 1 RIGHT 1
        //3 UP = y - 1 DOWN 2
        int x = gridCell.getX();
        int y = gridCell.getY();
        DungeonGeneration.GridCell room = null;

        switch (doorIndex) {
            case 0:
                if (x > 0) {
                    room = dungeonGeneration.getRoom(x - 1, y);
                    if (room == null) break;
                }
                break;
            case 1:
                room = dungeonGeneration.getRoom(x + 1, y);
                if (room == null) break;

                break;
            case 2:
                room = dungeonGeneration.getRoom(x, y + 1);
                if (room == null) break;
                break;
            case 3:
                if (y > 0) {
                    room = dungeonGeneration.getRoom(x, y - 1);
                    if (room == null) break;
                }
                break;
        }
        return room;
    }

    public static void closeOpposite(DungeonGeneration dungeonGeneration, int doorType, int doorIndex, DungeonGeneration.GridCell gridCell) {
        //2 DOWN = y + 1 UP 3
        //1 RIGHT = x + 1 LEFT 0
        //0 LEFT = x - 1 RIGHT 1
        //3 UP = y - 1 DOWN 2
        DungeonGeneration.GridCell room = getOpposite(dungeonGeneration, doorIndex, gridCell);
        int[] doors;
        if (room == null) return;
        doors = room.getDoors();

        if (doors != null) {
            doors[getOppositeDirection(doorIndex)] = doorType;
            room.setDoors(doors);
        }
    }

    // Define a method to get the opposite direction index
    private static int getOppositeDirection(int directionIndex) {
        switch (directionIndex) {
            case 0:
                return 1; // Opposite of left is right
            case 1:
                return 0; // Opposite of right is left
            case 2:
                return 3; // Opposite of down is up
            case 3:
                return 2; // Opposite of up is down
            default:
                return -1; // Invalid direction index
        }
    }
}
