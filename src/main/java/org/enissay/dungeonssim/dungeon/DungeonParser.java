package org.enissay.dungeonssim.dungeon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.Cuboid;
import org.enissay.dungeonssim.utils.LuckUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DungeonParser {

    public static void parse(final Dungeon dungeon, final DungeonGeneration dungeonGeneration, final Location location) {
        if (dungeon == null || dungeonGeneration == null || location == null) return;

        LinkedList<DungeonGeneration.GridCell> visited = new LinkedList<>();
        LinkedList<DungeonGeneration.GridCell> pastedRooms = new LinkedList<>();

        HashMap<DungeonGeneration.GridCell, String> roomNames = new HashMap<>();
        /**
         * FIRST ITERATION
         */
        dungeonGeneration.getRooms().forEach(gridCell -> {
            int input = dungeonGeneration.getGridMap()[gridCell.getX()][gridCell.getY()];
            DungeonTemplate template = DungeonHandler.getTemplateFromID(input);

            TempDungeonBuilds tempDungeonBuilds;
            String chosenRoom = LuckUtil.getRandomWeighted(template.getRoomsFromTemplate());
            if (gridCell.getDoors() != null) {
                for (int j = 0; j < gridCell.getDoors().length; j++) {
                    final DungeonGeneration.GridCell opposites = getOpposite(dungeonGeneration, j, gridCell);
                    if (opposites != null && dungeonGeneration.getGridMap()[opposites.getX()][opposites.getY()] != 1)
                        chosenRoom = template.getRoomsFromTemplate().keySet().stream().toList().get(0);
                }
            }
            /*if (roomNames.get(gridCell).contains("ROOM_2x1") &&
                    gridCell.getX() < dungeonGeneration.getGridWidth() &&
                    gridCell.getY() < dungeonGeneration.getGridHeight()) {
                for (int i = 0; i < 4; i++) {
                    if (x)
                    //X+1 Y RIGHT
                    //X Y+1 DOWN
                    //X Y-1 UP
                    //X-1 Y LEFT
                }

            }*/
            roomNames.put(gridCell, chosenRoom);
            tempDungeonBuilds = DungeonHandler.loadRoom(template.getName(), chosenRoom);

            Bukkit.broadcastMessage(ChatColor.GREEN + "FOUND FITTING ROOM FOR " + template.getName());

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

            Map<Integer, String> map = new HashMap<>();
            map.put(0, "LEFT");
            map.put(1, "RIGHT");
            map.put(2, "DOWN");
            map.put(3, "UP");

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
                        final int doorType = LuckUtil.getRandomWeighted(doorChance);
                        int random = dungeonGeneration.randomDoor(gridCell);
                        final DungeonGeneration.GridCell opposite = getOpposite(dungeonGeneration, random, gridCell);
                        final DungeonGeneration.GridCell neighbor = dungeonGeneration.getRoom(x + 1, y - 1);

                        //Try to prevent rare from spawning near boss
                        boolean canPass = true;
                        for (int j = 0; j < gridCell.getDoors().length; j++) {
                            final DungeonGeneration.GridCell opposites = getOpposite(dungeonGeneration, j, gridCell);
                            if (opposites != null && dungeonGeneration.getGridMap()[opposites.getX()][opposites.getY()] == 2) canPass = false;
                        }
                        if (canPass && neighbor != null && dungeonGeneration.getGridMap()[neighbor.getX()][neighbor.getY()] == 1 &&
                                !dungeonGeneration.roomHasKeyDoor(opposite) &&
                                opposite != null &&
                                dungeonGeneration.getGridMap()[opposite.getX()][opposite.getY()] == 1 &&
                                !visited.contains(gridCell) &&
                                !visited.contains(opposite)) {
                            gridCell.getDoors()[random] = doorType;//0 TO CLOSE NORMAL
                            closeOpposite(dungeonGeneration, doorType, random, gridCell);
                            visited.add(gridCell);
                            visited.add(opposite);
                            Bukkit.broadcastMessage("FOUND ONE " + doorType + " " + x + " " + y);
                        }
                    }
                }
            }
            else if (input == 1 && roomNames.get(gridCell).equals("ROOM_RARE")) {
                //boolean canPass = true;
                for (int availableDoor : dungeonGeneration.getAvailableDoors(gridCell)) {
                        /*int doorInput = gridCell.getDoors()[availableDoor];
                        doorInput = 2;*/
                    gridCell.getDoors()[availableDoor] = 2;
                    closeOpposite(dungeonGeneration, 2, availableDoor, gridCell);
                }
            }
            else if (input == 2) {
                for (int availableDoor : dungeonGeneration.getAvailableDoors(gridCell)) {
                        /*int doorInput = gridCell.getDoors()[availableDoor];
                        doorInput = 2;*/
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
        dungeonGeneration.getRooms().forEach((gridCell) -> {
            int input = dungeonGeneration.getGridMap()[gridCell.getX()][gridCell.getY()];
            DungeonTemplate template = DungeonHandler.getTemplateFromID(input);

            int x = gridCell.getX();
            int y = gridCell.getY();

            RoomRotation initialDirection = RoomRotation.SOUTH;//RoomRotation.values()[new Random().nextInt(RoomRotation.values().length)];

            int startX = /*(DungeonHandler.getDungeons().size() + dungeonGeneration.getGridWidth() * dungeonGeneration.getGridBlocks()) +*/ (location.getBlockX() + x * dungeonGeneration.getGridBlocks()) - (dungeonGeneration.getGridBlocks() * dungeonGeneration.getGridWidth()/2);
            int startY = (location.getBlockZ() + y * dungeonGeneration.getGridBlocks()) - (dungeonGeneration.getGridBlocks() * dungeonGeneration.getGridHeight()/2);

            TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(template.getName(), roomNames.get(gridCell));

            RoomPasting roomPastingSpawn = new RoomPasting(tempDungeonBuilds, new Location(location.getWorld(), startX, location.getBlockY(), startY), 100, initialDirection,
                    gridCell.getDoors());//LEFT, RIGHT, DOWN, UP
            final Cuboid cuboid = roomPastingSpawn.pasteTest(dungeon, dungeonGeneration.getGridBlocks(), false);
            dungeon.addRoom(new DungeonRoom(dungeon, template, roomNames.get(gridCell), cuboid, gridCell, dungeon.getRooms().size() + 1));
            pastedRooms.add(gridCell);
        });
        /*int minY = dungeonGeneration.getMinimumValidY();
        int maxY = dungeonGeneration.getMaximumValidY();
        if (!Objects.isNull(minY) && !Objects.isNull(maxY)) {
            Bukkit.broadcastMessage(minY + " " + maxY);
            int minX = Integer.MAX_VALUE;
            int maxX = 0;
            for (int i = 0; i < dungeonGeneration.getGridHeight(); i++) {
                int currentX = dungeonGeneration.getMinimumValidX(i);
                if (currentX < minX) minX = currentX;
                else if (dungeonGeneration.getMaximumValidX(i) > maxX) maxX = dungeonGeneration.getMaximumValidX(i);
            }
            final DungeonGeneration.GridCell minGridCell = dungeonGeneration.getRoom(minX, minY);
            final DungeonGeneration.GridCell maxGridCell = dungeonGeneration.getRoom(maxX, maxY);
            final AtomicReference<Location> maxLoc = new AtomicReference<>(), minLoc = new AtomicReference<>();
            dungeon.getRooms().forEach(room -> {
                final DungeonGeneration.GridCell gridCell = room.getGridCell();
                if (minGridCell != null && gridCell.getX() == minGridCell.getX() && gridCell.getY() == minGridCell.getY()) {
                    CuboidTest cuboidTest = new CuboidTest(room.getCuboid().getPoint1(), room.getCuboid().getPoint2());
                    minLoc.set(cuboidTest.getLowerCorner().add(0, -100, 0));
                } else if (maxGridCell != null && gridCell.getX() == maxGridCell.getX() && gridCell.getY() == maxGridCell.getY()) {
                    CuboidTest cuboidTest = new CuboidTest(room.getCuboid().getPoint1(), room.getCuboid().getPoint2());
                    maxLoc.set(cuboidTest.getUpperCorner().add(0, 100, 0));
                }
            });
            dungeon.setCuboid(new Cuboid(minLoc.get(), maxLoc.get()));
        }*/
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
        /*Map<Integer, String> map = new HashMap<>();
        map.put(0, "LEFT");
        map.put(1, "RIGHT");
        map.put(2, "DOWN");
        map.put(3, "UP");
        int x = gridCell.getX();
        int y = gridCell.getY();
        DungeonGeneration.GridCell room = null;
        int[] doors;

        switch (doorIndex) {
            case 0:
                if (x > 0) {
                    room = dungeonGeneration.getRoom(x - 1, y);
                    if (room == null) break;
                    doors = room.getDoors();
                    if (doors != null) {
                        doors[getOppositeDirection(doorIndex)] = doorType;
                        dungeonGeneration.getRoom(x - 1, y).setDoors(doors);
                    }
                }
                break;
            case 1:
                room = dungeonGeneration.getRoom(x + 1, y);
                if (room == null) break;
                doors = room.getDoors();
                if (doors != null) {
                    doors[getOppositeDirection(doorIndex)] = doorType;
                    dungeonGeneration.getRoom(x + 1, y).setDoors(doors);
                }
                break;
            case 2:
                room = dungeonGeneration.getRoom(x, y + 1);
                if (room == null) break;
                doors = room.getDoors();
                if (doors != null) {
                    doors[getOppositeDirection(doorIndex)] = doorType;
                    dungeonGeneration.getRoom(x, y + 1).setDoors(doors);
                }
                break;
            case 3:
                if (y > 0) {
                    room = dungeonGeneration.getRoom(x, y - 1);
                    if (room == null) break;
                    doors = room.getDoors();
                    if (doors != null) {
                        doors[getOppositeDirection(doorIndex)] = doorType;
                        dungeonGeneration.getRoom(x, y - 1).setDoors(doors);
                    }
                }
                break;
        }
        if (room != null)
            Bukkit.broadcastMessage("Changed opposite " + map.get(getOppositeDirection(doorIndex)) + " x: " + room.getX() + " y:" + room.getY() + " doorType: " + doorType);*/
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
