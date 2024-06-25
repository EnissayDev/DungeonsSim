package org.enissay.dungeonssim.dungeon.system;

import org.enissay.dungeonssim.dungeon.templates.RoomRotation;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DungeonGeneration {

    private final List<GridCell> rooms = new ArrayList<>();
    private Dungeon dungeon;
    private int gridHeight, gridWidth;
    private int MAX_GRID = 0;
    private int MIN_ROOM = 0, MAX_ROOM = 0;
    private int GRID_BLOCKS = 0;
    private int MAX_BLOCKS = 0;
    private int[][] GRID_MAP;//X Y
    private long SEED;
    private Random RANDOM;//add seed

    private List<GridCell> keyRooms = new ArrayList<>();

    public DungeonGeneration(final long seed, final Dungeon dungeon) {
        this.SEED = seed;
        this.dungeon = dungeon;
        this.RANDOM = new Random(seed);
    }

    public DungeonGeneration(final Dungeon dungeon) {
        this.dungeon = dungeon;
        this.SEED = new Random().nextInt(100_000_000, 1_100_000_000);
        this.RANDOM = new Random(SEED);
    }

    public DungeonGeneration setGridOptions(final int gridWidth, final int gridHeight) {
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        return this;
    }

    public DungeonGeneration setMaxRooms(final int maxRooms) {
        this.MAX_ROOM = maxRooms;
        return this;
    }

    public DungeonGeneration setMinRooms(final int minRooms) {
        this.MIN_ROOM = minRooms;
        return this;
    }

    public DungeonGeneration setGridBlocks(final int gridBlocks) {
        this.GRID_BLOCKS = gridBlocks;
        return this;
    }

    public DungeonGeneration build() {
        if (this.gridWidth > 0 && this.gridHeight > 0 && this.GRID_BLOCKS > 0) {
            MAX_GRID = gridHeight * gridWidth;
            //MIN_GRID = MAX_GRID / 2 + MAX_GRID / 3;
            MAX_BLOCKS = MAX_GRID * GRID_BLOCKS;
            GRID_MAP = new int[gridWidth][gridHeight];
            generateDungeon(0, 0);
            dungeon.setDungeonGeneration(this);
        }
        return this;
    }

    public void setSeed(long seed) {
        this.SEED = seed;
        this.RANDOM = new Random(seed);
    }

    public long getSeed() {
        return SEED;
    }

    public Random getRandom() {
        return RANDOM;
    }

    public List<GridCell> getRooms() {
        return rooms;
    }

    public int[][] getGridMap() {
        return GRID_MAP;
    }

    public class GridCell {
        int X, Y, ID;
        RoomRotation direction;
        int[] doors;//LEFT, RIGHT, DOWN, UP

        public GridCell(int ID, int x, int y, RoomRotation direction) {
            this.ID = ID;
            X = x;
            Y = y;
            this.direction = direction;
        }

        public int getID() {
            return ID;
        }

        public int getX() {
            return X;
        }

        public int getY() {
            return Y;
        }

        public RoomRotation getDirection() {
            return direction;
        }

        public void setDoors(int[] doors) {
            this.doors = doors;
        }

        public int[] getDoors() {
            return doors;
        }

        public boolean hasLeftDoor() {
            return doors != null ? doors[0] == 1 : false;
        }

        public boolean hasRightDoor() {
            return doors != null ? doors[1] == 1 : false;
        }

        public boolean hasDownDoor() {
            return doors != null ? doors[2] == 1 : false;
        }

        public boolean hasUpDoor() {
            return doors != null ? doors[3] == 1 : false;
        }
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getGridBlocks() {
        return GRID_BLOCKS;
    }

    public void generateDungeon(int startX, int startY) {
        int[] directionX = {-1, 1, 0, 0};
        int[] directionY = {0, 0, -1, 1};

        int roomCount = 0; // Track the number of rooms generated
        int attempt = 0;
        //Bukkit.broadcastMessage("1");
        while (roomCount < MIN_ROOM || roomCount > MAX_ROOM) {
            attempt++;
            System.out.println("attempt: " + attempt);
            resetGrid(GRID_MAP);
            rooms.clear();

            // Generate the dungeon
            roomCount = 0;
            int currentX = startX;
            int currentY = startY;

            System.out.println("attempts: " + attempt);

            for (int i = 0; i < 100; i++) {
                int randomDirection = RANDOM.nextInt(4);

                int newX = currentX + directionX[randomDirection];
                int newY = currentY + directionY[randomDirection];

                if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight && GRID_MAP[newX][newY] != 1) {

                    GRID_MAP[newX][newY] = 1;
                    RoomRotation direction;
                    // Determine the direction
                    if (newX > currentX) {
                        direction = RoomRotation.EAST;
                    } else if (newX < currentX) {
                        direction = RoomRotation.WEST;
                    } else if (newY > currentY) {
                        direction = RoomRotation.SOUTH;
                    } else {
                        direction = RoomRotation.NORTH;
                    }

                    rooms.add(new GridCell(rooms.size() + 1, newX, newY, direction));

                    currentX = newX;
                    currentY = newY;

                    roomCount++;
                }
            }
        }

        final int minY = getMinimumValidY();
        final int maxY = getMaximumValidY();
        System.out.println("Minimum Y : " + minY);
        System.out.println("Maximum Y : " + maxY);
        final int randCol = RANDOM.nextInt(minY, minY + 1);
        final int randMaxCol = getMaximumValidY();//RANDOM.nextInt(maxY - 1, maxY);
        int randSX = getMinimumValidX(randCol);//getRandomRowInColumn(randCol, RANDOM);
        if ((randSX + 1 <= gridWidth - 1) && GRID_MAP[randSX + 1][randCol] != 0) randSX = getMaximumValidX(randCol);
        final int randBX = getMaximumValidX(randMaxCol);//getRandomRowInColumn(randMaxCol, RANDOM);
        System.out.println("Found for spawn x: " + randSX + " y: " + randCol);
        System.out.println("Found for boss x: " + randBX + " y: " + randMaxCol);
        //GRID_MAP[randSX][randCol] = 3;

        getRooms().forEach(room -> {
            if (room.getID() == rooms.size())
                GRID_MAP[room.getX()][room.getY()] = 2;
            else if (room.getID() == 1) GRID_MAP[room.getX()][room.getY()] = 3;
        });

        //GRID_MAP[randBX][randMaxCol] = 2;
        System.out.println("Rooms generated: " + rooms.size());
        printGrid(GRID_MAP, rooms);

    }

    public int getMinimumValidY() {
        GridCell gridCell = rooms.stream().min(Comparator.comparing(GridCell::getY)).orElse(null);
        return gridCell != null ? gridCell.getY() : null;
    }

    public int getMinimumValidX(int columnY) {
        AtomicInteger minX = new AtomicInteger(Integer.MAX_VALUE);
        rooms.forEach(room -> {
            if (room.getY() == columnY) {
                if (room.getX() < minX.get()) minX.set(room.getX());
            }
        });
        return minX.get();
    }

    public int getMaximumValidX(int columnY) {
        AtomicInteger maxX = new AtomicInteger(0);
        rooms.forEach(room -> {
            if (room.getY() == columnY) {
                if (room.getX() > maxX.get()) maxX.set(room.getX());
            }
        });
        return maxX.get();
    }

    public int getMaximumValidY() {
        //rooms.forEach(room -> System.out.println(room.X + " " + room.Y));
        GridCell gridCell = rooms.stream().max(Comparator.comparing(GridCell::getY)).orElse(null);
        return gridCell != null ? gridCell.getY() : null;
    }
    private  int getRandomRowInColumn(int columnY, Random random) {
        //System.out.println(getRoomsInColumn(columnY).size() + " for Y: " + columnY);
        List<Integer> availableRows = new ArrayList<>();
        for (DungeonGeneration.GridCell cell : getRoomsInColumn(columnY)) {
            if (cell.getY() == columnY) {
                availableRows.add(cell.getX());
            }
        }
        // Select a random row from the available rows
        return availableRows.size() > 0 ? availableRows.get(random.nextInt(0, availableRows.size())) : 0;
    }

    public GridCell getRoom(final int x, final int y) {
        AtomicReference<GridCell> foundRoom = new AtomicReference<>();
        rooms.forEach(room -> {
            if (room.getX() == x && room.getY() == y) foundRoom.set(room);
        });
        return foundRoom.get();
    }

    public int randomDoor(final GridCell gridCell) {
        int[] availableDoorIndexes = getAvailableDoors(gridCell); // Get available door indexes

        // If no available doors, return -1 or handle accordingly
        if (availableDoorIndexes.length == 0) {
            return -1; // or throw an exception, log a message, etc.
        }

        // Select a random index from the available door indexes
        int randomIndex = RANDOM.nextInt(availableDoorIndexes.length);

        // Return the index of the randomly selected door
        return availableDoorIndexes[randomIndex];
    }

    //how tf does this work??? weirdest shit i've ever done
    public List<GridCell> getKeyRooms() {
        return keyRooms;
    }

    public boolean roomHasKeyDoor(GridCell gridCell) {
        boolean result = false;
        if (getAvailableDoors(gridCell) != null && getAvailableDoors(gridCell).length > 0) {
            for (int availableDoor : getAvailableDoors(gridCell)) {
                if (gridCell.getDoors()[availableDoor] == 2) result = true;
            }
        }
        return result;
    }

    public int[] getAvailableDoors(GridCell gridCell) {
        int[] doors = null, indexes = null;
        int count = 0;

        if (gridCell != null && gridCell.getDoors() != null) {
            doors = gridCell.getDoors();
            indexes = new int[doors.length];

            for (int i = 0; i < doors.length; i++) {
                if (doors[i] == 1) {
                    indexes[count] = i;
                    count++;
                }
            }
        }
        // Resize indexes array to remove unused positions
        if (indexes != null)
        indexes = Arrays.copyOf(indexes, count);

        return indexes;
    }

    public  List<DungeonGeneration.GridCell> getRoomsInColumn(final int columnY) {
        List<DungeonGeneration.GridCell> gridCells = new ArrayList<>();
        for (int x = 0; x < gridWidth; x++) {
            System.out.println("Checking for x: " + x + " in y:" + columnY + " " + GRID_MAP[x][columnY]);
            if (GRID_MAP[x][columnY] != 0) {
                System.out.println("Found valid room for x: " + x + " in y:" + columnY);
                int finalI = x;
                gridCells.add(rooms.stream().filter(grid -> grid.getX() == finalI && grid.getY() == columnY).findFirst().get());
            }
        }
        return gridCells;
    }

    private  void resetGrid(int[][] GRID_MAP) {
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                GRID_MAP[i][j] = 0;
            }
        }
    }

    public GridCell getTemplate(final int index) {
        return rooms.stream().filter(grid -> GRID_MAP[grid.getX()][grid.getY()] == index).findFirst().orElse(null);
    }

    private  void printGrid(int[][] GRID_MAP, List<DungeonGeneration.GridCell> rooms) {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                String c = " ";
                if (GRID_MAP[x][y] == 1) {
                    RoomRotation roomDirection = findRoomDirection(rooms, x, y);
                    if (roomDirection != null) {
                        c = switch (roomDirection) {
                            case NORTH -> "^";
                            case SOUTH -> "d";
                            case EAST -> ">";
                            case WEST -> "<";
                        };
                    }
                } else if (GRID_MAP[x][y] == 3) {
                    c = "S"; // Spawn room
                } else if (GRID_MAP[x][y] == 2) {
                    c = "B"; // Boss room
                }
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private static RoomRotation findRoomDirection(List<DungeonGeneration.GridCell> rooms, int x, int y) {
        for (DungeonGeneration.GridCell room : rooms) {
            if (room.getX() == x && room.getY() == y) {
                return room.getDirection();
            }
        }
        return null;
    }
}
