package org.enissay.dungeonssim.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.*;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.ItemUtils;
import org.enissay.dungeonssim.utils.LuckUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
    void test(@Context CommandSender sender, @Arg int MIN_ROOM, @Arg int MAX_ROOM) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final LinkedList<Player> players = new LinkedList<>();
            players.add(player);
            sendInfo(player, "Creating dungeon instance...");
            final Dungeon dungeon = new Dungeon(0);
            final DungeonGeneration dungeonGeneration = new DungeonGeneration(dungeon);
            dungeonGeneration.setGridBlocks(33)
                    .setGridOptions(6, 6)
                    .setMinRooms(MIN_ROOM)
                    .setMaxRooms(MAX_ROOM)
                    .build();

            sendInfo(player, "Dungeon algorithm...");
            //dungeonGeneration.generateDungeon(0, 0);

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
                final String chosenRoom = LuckUtil.getRandomWeighted(template.getRoomsFromTemplate());
                roomNames.put(gridCell, chosenRoom);
                tempDungeonBuilds = DungeonHandler.loadRoom(template.getName(), chosenRoom);

                sendNormal(player, "FOUND FITTING ROOM FOR " + template.getName());

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
                 * AND PREVENT FROM ROOM_2 TO SPAWN NEAR SPAWN_ROOM
                 */
                if (input == 1 && !roomNames.get(gridCell).equals("ROOM_2")) {
                    if (dungeonGeneration.getAvailableDoors(gridCell).length > 2) {
                        for (int i = 0; i < 4 - dungeonGeneration.getAvailableDoors(gridCell).length; i++) {
                            final int doorType = LuckUtil.getRandomWeighted(doorChance);
                            int random = dungeonGeneration.randomDoor(gridCell);
                            final DungeonGeneration.GridCell opposite = getOpposite(dungeonGeneration, random, gridCell);
                            final DungeonGeneration.GridCell neighbor = dungeonGeneration.getRoom(x + 1, y - 1);

                            if (neighbor != null && dungeonGeneration.getGridMap()[neighbor.getX()][neighbor.getY()] == 1 &&
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
                else if (input == 1 && roomNames.get(gridCell).equals("ROOM_2")) {
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

                if (x < dungeonGeneration.getGridWidth() - 1 &&
                        y < dungeonGeneration.getGridHeight() - 1 &&
                        dungeonGeneration.getGridMap()[x][y] == 1 &&
                        dungeonGeneration.getGridMap()[x + 1][y] == 1 &&
                        dungeonGeneration.getGridMap()[x][y + 1] == 1 &&
                        dungeonGeneration.getGridMap()[x + 1][y + 1] == 1 &&
                        !visited.contains(dungeonGeneration.getRoom(x, y)) &&
                        !visited.contains(dungeonGeneration.getRoom(x + 1, y)) &&
                        !visited.contains(dungeonGeneration.getRoom(x, y + 1)) &&
                        !visited.contains(dungeonGeneration.getRoom(x + 1, y + 1))) {

                    /*visited.add(dungeonGeneration.getRoom(x, y));
                    visited.add(dungeonGeneration.getRoom(x + 1, y));
                    visited.add(dungeonGeneration.getRoom(x, y + 1));
                    visited.add(dungeonGeneration.getRoom(x + 1, y + 1));*/

                    // Randomly choose up to two doors to close for each room
                    /*int closedDoorCount = 0;
                    for (int i = 0; i < 4; i++) {
                        DungeonGeneration.GridCell room = dungeonGeneration.getRoom(x + (i % 2), y + (i / 2));
                        int[] newDoors = room.getDoors(); // Initialize doors array for the current room

                        if (closedDoorCount < 2) {
                            int doorIndex = random.nextInt(4);

                            if (newDoors != null && newDoors[doorIndex] == 1) {
                                //newDoors[doorIndex] = 0;
                                //room.setDoors(newDoors);
                                Bukkit.broadcastMessage("Randomly removed " + map.get(doorIndex) + " x: " + room.getX() + " " + "y: " + room.getY());

                                //2 DOWN = y + 1 UP
                                //1 RIGHT = x + 1 LEFT
                                //0 LEFT = x - 1 RIGHT
                                //3 UP = y - 1 DOWN
                                //closeOpposite(dungeonGeneration, 0,  doorIndex, room);
                                closedDoorCount++;
                            }
                        }
                    }*/
                }

                /*if (input == 2 || (tempDungeonBuilds.getTemplateName().equals("NORMAL_ROOM") && !tempDungeonBuilds.getName().equals("ROOM_1"))) {
                    gridCell.setDoors(new int[]{0, 0, 0, 0});
                    for (int i = 0; i < 4; i++)
                        closeOpposite(dungeonGeneration, 0, i, gridCell);
                }

                if (input == 1 && (tempDungeonBuilds.getTemplateName().equals("NORMAL_ROOM") && !tempDungeonBuilds.getName().equals("ROOM_1"))) {
                    final DungeonGeneration.GridCell room = gridCell;
                    int[] newDoors = room.getDoors(); // Initialize doors array for the current room

                    int doorIndex = 0;
                    if (dungeonGeneration.getRoom(x + 1, y) != null) doorIndex = 1;
                    else if (dungeonGeneration.getRoom(x, y + 1) != null) doorIndex = 3;
                    else if (dungeonGeneration.getRoom(x - 1, y) != null) doorIndex = 0;
                    else if (dungeonGeneration.getRoom(x, y - 1) != null) doorIndex = 2;

                    if (newDoors != null && newDoors[doorIndex] == 0) {
                        newDoors[doorIndex] = 2;
                        room.setDoors(newDoors);
                        Bukkit.broadcastMessage("Added a rare door " + map.get(doorIndex) + " x: " + room.getX() + " " + "y: " + room.getY());

                        //2 DOWN = y + 1 UP
                        //1 RIGHT = x + 1 LEFT
                        //0 LEFT = x - 1 RIGHT
                        //3 UP = y - 1 DOWN
                        closeOpposite(dungeonGeneration, 2, doorIndex, room);

                    }
                }

                //BOSS DOORS HANDLER
                if (input == 2) {
                    final DungeonGeneration.GridCell room = gridCell;
                    int[] newDoors = room.getDoors(); // Initialize doors array for the current room

                    int doorIndex = 0;
                    if (dungeonGeneration.getRoom(x + 1, y) != null) doorIndex = 1;
                    else if (dungeonGeneration.getRoom(x, y + 1) != null) doorIndex = 3;
                    else if (dungeonGeneration.getRoom(x - 1, y) != null) doorIndex = 0;
                    else if (dungeonGeneration.getRoom(x, y - 1) != null) doorIndex = 2;

                    if (newDoors != null && newDoors[doorIndex] == 0) {
                        newDoors[doorIndex] = 2;
                        room.setDoors(newDoors);
                        Bukkit.broadcastMessage("Added a boss door " + map.get(doorIndex) + " x: " + room.getX() + " " + "y: " + room.getY());

                        //2 DOWN = y + 1 UP
                        //1 RIGHT = x + 1 LEFT
                        //0 LEFT = x - 1 RIGHT
                        //3 UP = y - 1 DOWN
                        closeOpposite(dungeonGeneration, 2, doorIndex, room);
                    }
                }
                if (input == 2 || (input == 1 && !tempDungeonBuilds.getName().equals("ROOM_1"))) {
                    final DungeonGeneration.GridCell room = gridCell;
                    int[] newDoors = room.getDoors(); // Initialize doors array for the current room
                    Bukkit.broadcastMessage(newDoors.toString() + " " + input);
                    for (int i = 0; i < newDoors.length; i++) {
                        if (newDoors[i] == 0) {
                            closeOpposite(dungeonGeneration, 0, i, room);
                        }
                    }
                }*/
            });


            sendInfo(player, "Setup for doors finished...");
            sendInfo(player, "Final doors verification...");
            dungeonGeneration.getRooms().forEach(room -> {
                if (room.getDoors() != null) {
                    for (int i = 0; i < room.getDoors().length; i++) {
                        if (room.getDoors()[i] != 1)
                            closeOpposite(dungeonGeneration, room.getDoors()[i], i, room);
                    }
                }
            });

            final Generator2D generator2D = new Generator2D(dungeonGeneration, dungeonGeneration.getGridHeight(), dungeonGeneration.getGridWidth(), dungeonGeneration.getGridMap()/*dungeonGeneration.getGridMap()*/, dungeonGeneration.getGridBlocks(), roomNames);
            generator2D.generateMap(player.getLocation(), player.getWorld());

            final ItemStack item = ItemUtils.item(Material.FILLED_MAP, "map", "");
            MapMeta meta = (MapMeta) item.getItemMeta();
            MapView view = Bukkit.createMap(player.getWorld());

            view.getRenderers().clear();
            view.addRenderer(generator2D);
            view.setTrackingPosition(true);
            view.setUnlimitedTracking(true);
            view.setCenterX(player.getLocation().getBlockX());
            view.setCenterZ(player.getLocation().getBlockZ());
            view.setScale(MapView.Scale.CLOSE);
            meta.setMapView(view);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);














            /**
             * SECOND ITERATION
             */
            dungeonGeneration.getRooms().forEach((gridCell) -> {
                int input = dungeonGeneration.getGridMap()[gridCell.getX()][gridCell.getY()];
                DungeonTemplate template = DungeonHandler.getTemplateFromID(input);

                int x = gridCell.getX();
                int y = gridCell.getY();

                RoomRotation initialDirection = RoomRotation.SOUTH;

                int startX = (player.getLocation().getBlockX() + x * dungeonGeneration.getGridBlocks()) - (dungeonGeneration.getGridBlocks() * dungeonGeneration.getGridWidth()/2);
                int startY = (player.getLocation().getBlockZ() + y * dungeonGeneration.getGridBlocks()) - (dungeonGeneration.getGridBlocks() * dungeonGeneration.getGridHeight()/2);

                TempDungeonBuilds tempDungeonBuilds = DungeonHandler.loadRoom(template.getName(), roomNames.get(gridCell));

                RoomPasting roomPastingSpawn = new RoomPasting(tempDungeonBuilds, new Location(player.getWorld(), startX, player.getLocation().getBlockY(), startY), 100, initialDirection,
                        gridCell.getDoors());//LEFT, RIGHT, DOWN, UP
                roomPastingSpawn.pasteTest(dungeonGeneration.getGridBlocks());
                pastedRooms.add(gridCell);
            });
            player.sendMessage("Dungeon generated!");
        }
    }

    public DungeonGeneration.GridCell getOpposite(DungeonGeneration dungeonGeneration, int doorIndex, DungeonGeneration.GridCell gridCell) {
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

    public void closeOpposite(DungeonGeneration dungeonGeneration, int doorType, int doorIndex, DungeonGeneration.GridCell gridCell) {
        //2 DOWN = y + 1 UP 3
        //1 RIGHT = x + 1 LEFT 0
        //0 LEFT = x - 1 RIGHT 1
        //3 UP = y - 1 DOWN 2
        Map<Integer, String> map = new HashMap<>();
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
            Bukkit.broadcastMessage("Changed opposite " + map.get(getOppositeDirection(doorIndex)) + " x: " + room.getX() + " y:" + room.getY() + " doorType: " + doorType);
    }

    // Define a method to get the opposite direction index
    private int getOppositeDirection(int directionIndex) {
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
