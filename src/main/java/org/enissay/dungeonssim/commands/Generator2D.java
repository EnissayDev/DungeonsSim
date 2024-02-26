package org.enissay.dungeonssim.commands;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonBuilds;
import org.enissay.dungeonssim.dungeon.DungeonGeneration;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Generator2D extends MapRenderer {

    private int gridHeight, gridWidth;
    private int MAX_GRID = 0;
    private int MIN_GRID = 0;
    private int GRID_BLOCKS = 32;
    private int MAX_BLOCKS = 0;
    private int[][] GRID_MAP;
    private BufferedImage image;
    private DungeonGeneration dungeonGeneration;
    private HashMap<DungeonGeneration.GridCell, String> roomNames;

    public Generator2D(DungeonGeneration dungeonGeneration, int gridHeight, int gridWidth, int[][] GRID_MAP, int GRID_BLOCKS, HashMap<DungeonGeneration.GridCell, String> roomNames) {
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        MAX_GRID = gridHeight * gridWidth;
        MIN_GRID = MAX_GRID / 2 + MAX_GRID / 3;
        MAX_BLOCKS = MAX_GRID * GRID_BLOCKS;
        this.GRID_MAP = GRID_MAP;
        this.roomNames = roomNames;
        this.dungeonGeneration = dungeonGeneration;
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        mapCanvas.setPixelColor(0, 0, Color.RED);
        mapCanvas.drawImage(0, 0, image);
        MapCursorCollection cursors = new MapCursorCollection();
        //cursors.addCursor(60, 70, (byte) 12, (byte) 4, true);
        mapCanvas.setCursors(cursors);
    }

    /*private void generateDungeon(Location startLocation) {
        final Random random = new Random();
        int roomsNumber = random.nextInt((MAX_GRID - MIN_GRID) + 1) + MIN_GRID;
        for (int i = 0; i < roomsNumber; i++) {
            int chosenX = random.nextInt(gridWidth);
            int chosenY = random.nextInt(gridHeight);

            if (chosenX >= 0 && chosenX < gridWidth && chosenY >= 0 && chosenY < gridHeight) {
                GRID_MAP[chosenX][chosenY] = 1; // Mark the chosen cell as part of the dungeon
                //rooms.put(new GridCell(chosenX, chosenY), DungeonHandler.getTemplate("NORMAL_ROOM"));
                final GridCell gridCell = new GridCell(chosenX, chosenY);
                if (GRID_MAP[chosenX][chosenY] == 1) {
                    if (chosenX >= 0 && chosenY >= 0 && chosenY <= 2
                            && !templateExists("SPAWN_ROOM")) {
                        rooms.put(gridCell, DungeonHandler.getTemplate("SPAWN_ROOM"));
                    } else if (chosenX >= 0 && chosenY > 4 && chosenY <= gridHeight &&
                            !templateExists("BOSS_ROOM")) {
                        rooms.put(gridCell, DungeonHandler.getTemplate("BOSS_ROOM"));
                    } else if (!verified(chosenX, chosenY)) {
                        rooms.put(gridCell, DungeonHandler.getTemplate("NORMAL_ROOM"));
                    }
                }
                if (isEmptySurroundings(chosenX, chosenY)) {
                    int[] neighbor = findNeighbor(chosenX, chosenY);

                    if (neighbor != null) {
                        final GridCell gridCell2 = new GridCell(neighbor[0], neighbor[1]);
                        //rooms.put(gridCell2, DungeonHandler.getTemplate("NORMAL_ROOM"));
                        connectCells(chosenX, chosenY, neighbor[0], neighbor[1]);
                        if (GRID_MAP[chosenX][chosenY] == 1) {
                            if (chosenX >= 0 && chosenY >= 0 && chosenY <= 2
                                    && !templateExists("SPAWN_ROOM")) {
                                rooms.put(gridCell2, DungeonHandler.getTemplate("SPAWN_ROOM"));
                            } else if (chosenX >= 0 && chosenY > 4 && chosenY <= gridHeight &&
                                    !templateExists("BOSS_ROOM")) {
                                rooms.put(gridCell2, DungeonHandler.getTemplate("BOSS_ROOM"));
                            } else if (!verified(chosenX, chosenY)) {
                                rooms.put(gridCell2, DungeonHandler.getTemplate("NORMAL_ROOM"));
                            }
                        }
                    }
                }else if (hasNoSurroundingCells(chosenX, chosenY)) {
                    GRID_MAP[chosenX][chosenY] = 0;
                    //if (verified(chosenX, chosenY)) rooms.remove(getGridCellFromCoords(chosenX, chosenY));
                }
            }
        }
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (GRID_MAP[x][y] == 1) {
                    final GridCell gridCell = new GridCell(x, y);

                    rooms2.put(gridCell, DungeonHandler.getTemplate("NORMAL_ROOM"));
                }
            }
        }
        rooms.forEach((gridCell, template) -> {
            Bukkit.broadcastMessage("x: " + gridCell.getX() + " y: " + gridCell.getY() + " -> " + template.getName());
        });
    }

    public Map<GridCell, DungeonTemplate> getRooms2() {
        return rooms2;
    }

    public boolean verified(final int x, final int y) {
        AtomicBoolean exists = new AtomicBoolean(false);
        rooms.keySet().forEach(gridCell -> {
            if (gridCell.getX() == x && gridCell.getY() == y) exists.set(true);
        });
        return exists.get();
    }

    public GridCell getGridCellFromCoords(final int x, final int y) {
        AtomicReference<GridCell> cell = new AtomicReference<>();
        rooms.keySet().forEach(gridCell -> {
            if (gridCell.getX() == x && gridCell.getY() == y) cell.set(gridCell);
        });
        return cell.get();
    }

    public List<GridCell> getTemplates(final String templateName) {
        List<GridCell> list = new ArrayList<>();

        rooms.entrySet().forEach(entry -> {
            if (entry.getValue().getName().equals(templateName)) list.add(entry.getKey());
        });
        return list;
    }

    public boolean templateExists(String templateName) {
        return rooms.values().contains(DungeonHandler.getTemplate(templateName));
    }

    public DungeonTemplate searchTemplate(final GridCell cell) {
        AtomicReference<DungeonTemplate> template = new AtomicReference<>();
        rooms.entrySet().forEach(entry -> {
            if (entry.getKey().getX() == cell.getX() && entry.getKey().getY() == cell.getY())
                template.set(entry.getValue());
        });
        return template.get();
    }

    // Check if the chosen cell has no surrounding cells
    private boolean hasNoSurroundingCells(int x, int y) {
        boolean isAtLeftEdge = x == 0;
        boolean isAtRightEdge = x == gridWidth - 1;
        boolean isAtTopEdge = y == 0;
        boolean isAtBottomEdge = y == gridHeight - 1;

        // Check if any adjacent cell is a part of the dungeon
        boolean hasLeftNeighbor = !isAtLeftEdge && GRID_MAP[x - 1][y] == 1;
        boolean hasRightNeighbor = !isAtRightEdge && GRID_MAP[x + 1][y] == 1;
        boolean hasTopNeighbor = !isAtTopEdge && GRID_MAP[x][y - 1] == 1;
        boolean hasBottomNeighbor = !isAtBottomEdge && GRID_MAP[x][y + 1] == 1;
        boolean hasTopLeftNeighbor = !isAtTopEdge && !isAtLeftEdge && GRID_MAP[x - 1][y - 1] == 1;
        boolean hasTopRightNeighbor = !isAtTopEdge && !isAtRightEdge && GRID_MAP[x + 1][y - 1] == 1;
        boolean hasBottomLeftNeighbor = !isAtBottomEdge && !isAtLeftEdge && GRID_MAP[x - 1][y + 1] == 1;
        boolean hasBottomRightNeighbor = !isAtBottomEdge && !isAtRightEdge && GRID_MAP[x + 1][y + 1] == 1;

        // Return true if there are no surrounding cells
        return !hasLeftNeighbor && !hasRightNeighbor && !hasTopNeighbor && !hasBottomNeighbor &&
                !hasTopLeftNeighbor && !hasTopRightNeighbor && !hasBottomLeftNeighbor && !hasBottomRightNeighbor;
    }

    // Check if the surroundings of the given cell are empty
    private boolean isEmptySurroundings(int x, int y) {
        return (x > 0 && GRID_MAP[x - 1][y] == 0) ||                    // Left
                (x < gridWidth - 1 && GRID_MAP[x + 1][y] == 0) ||        // Right
                (y > 0 && GRID_MAP[x][y - 1] == 0) ||                    // Up
                (y < gridHeight - 1 && GRID_MAP[x][y + 1] == 0) ||       // Down
                (x > 0 && y > 0 && GRID_MAP[x - 1][y - 1] == 0) ||       // Upper-left
                (x > 0 && y < gridHeight - 1 && GRID_MAP[x - 1][y + 1] == 0) ||  // Lower-left
                (x < gridWidth - 1 && y > 0 && GRID_MAP[x + 1][y - 1] == 0) ||   // Upper-right
                (x < gridWidth - 1 && y < gridHeight - 1 && GRID_MAP[x + 1][y + 1] == 0); // Lower-right
    }

    // Find a neighboring cell with a value of 1
    private int[] findNeighbor(int x, int y) {
        List<int[]> neighbors = new ArrayList<>();

        // Check if left neighbor exists and is part of the dungeon
        if (x > 0 && GRID_MAP[x - 1][y] == 1)
            neighbors.add(new int[]{x - 1, y});

        // Check if right neighbor exists and is part of the dungeon
        if (x < gridWidth - 1 && GRID_MAP[x + 1][y] == 1)
            neighbors.add(new int[]{x + 1, y});

        // Check if top neighbor exists and is part of the dungeon
        if (y > 0 && GRID_MAP[x][y - 1] == 1)
            neighbors.add(new int[]{x, y - 1});

        // Check if bottom neighbor exists and is part of the dungeon
        if (y < gridHeight - 1 && GRID_MAP[x][y + 1] == 1)
            neighbors.add(new int[]{x, y + 1});

        // Check if top-left neighbor exists and is part of the dungeon
        if (x > 0 && y > 0 && GRID_MAP[x - 1][y - 1] == 1)
            neighbors.add(new int[]{x - 1, y - 1});

        // Check if top-right neighbor exists and is part of the dungeon
        if (x < gridWidth - 1 && y > 0 && GRID_MAP[x + 1][y - 1] == 1)
            neighbors.add(new int[]{x + 1, y - 1});

        // Check if bottom-left neighbor exists and is part of the dungeon
        if (x > 0 && y < gridHeight - 1 && GRID_MAP[x - 1][y + 1] == 1)
            neighbors.add(new int[]{x - 1, y + 1});

        // Check if bottom-right neighbor exists and is part of the dungeon
        if (x < gridWidth - 1 && y < gridHeight - 1 && GRID_MAP[x + 1][y + 1] == 1)
            neighbors.add(new int[]{x + 1, y + 1});

        if (!neighbors.isEmpty()) {
            // Return a random neighboring cell
            return neighbors.get(new Random().nextInt(neighbors.size()));
        }
        return null;
    }

    // Connect two cells by marking the path between them
    private void connectCells(int x1, int y1, int x2, int y2) {
        // Find the direction of the connection
        int dx = Integer.compare(x2, x1);
        int dy = Integer.compare(y2, y1);

        // Connect cells by creating a passage
        if (dx != 0) {
            // Connect horizontally
            int passageX = (x1 + x2) / 2;
            if (passageX >= 0 && passageX < gridWidth) {
                for (int y = Math.max(y1, y2) - 1; y <= Math.max(y1, y2) + 1; y++) {
                    if (y >= 0 && y < gridHeight) {
                        GRID_MAP[passageX][y] = 1;
                        //rooms.put(new GridCell(passageX, y), DungeonHandler.getTemplate("NORMAL_ROOM"));
                    }
                }
            }
        } else if (dy != 0) {
            // Connect vertically
            int passageY = (y1 + y2) / 2;
            if (passageY >= 0 && passageY < gridHeight) {
                for (int x = Math.max(x1, x2) - 1; x <= Math.max(x1, x2) + 1; x++) {
                    if (x >= 0 && x < gridWidth) {
                        GRID_MAP[x][passageY] = 1;
                        //rooms.put(new GridCell(x, passageY), DungeonHandler.getTemplate("NORMAL_ROOM"));
                    }
                }
            }
        }
    }*/

    /*public DungeonTemplate searchTemplate(final GridCell cell) {
        AtomicReference<DungeonTemplate> template = new AtomicReference<>();
        rooms.entrySet().forEach(entry -> {
            if (entry.getKey().getX() == cell.getX() && entry.getKey().getY() == cell.getY())
                template.set(entry.getValue());
        });
        return template.get();
    }*/

    public BufferedImage generateMap(Location startLocation, World world) {
        image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

        MapView view = Bukkit.createMap(world);
        view.getRenderers().clear();
        view.setTrackingPosition(true);
        view.setUnlimitedTracking(true);
        view.setScale(MapView.Scale.CLOSE);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        Color c1 = new Color(183, 165, 130, 255);
        Color c2 = new Color(161, 144, 114, 255);

        for (int l = 0; l < 128; l++)
            for (int i = 0; i < 128; i++) {
                if (i % 2 == 0)
                    g2.setColor(c1);
                else
                    g2.setColor(c2);
                g2.drawRect(i, l, i + 1, l + 1);
            }

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {

                //final GridCell gridCell = new GridCell(x, y);
                //final DungeonTemplate dungeonTemplate = searchTemplate(gridCell);

                /*if (rooms.get(gridCell) != null) Bukkit.broadcastMessage(ChatColor.GREEN + "FOUND: " + dungeonTemplate.getName());*/
                double dx = startLocation.getX() - view.getCenterX();
                double dz = startLocation.getZ() - view.getCenterZ();
                double angle = Math.atan2(dz, dx);
                double distance = Math.sqrt(dx * dx + dz * dz);

                double scale = 1.0 / (view.getScale().getValue() * 128.0);
                int newX = (int) Math.round(distance * scale * Math.cos(angle));
                int newZ = (int) Math.round(distance * scale * Math.sin(angle));
                /*int currx = (newX + x * GRID_BLOCKS) / 2 ;
                int curry = (newZ + y * GRID_BLOCKS) / 2 ;*/
                int currx = (x * GRID_BLOCKS) / 2 + GRID_BLOCKS;
                int curry = (y * GRID_BLOCKS) / 2 + GRID_BLOCKS;

                if (GRID_MAP != null) {
                    //g2.setColor(new Color(113, 67, 27, 255));
                    if (GRID_MAP[x][y] == 2) {
                        g2.setColor(Color.RED);
                    }else if (GRID_MAP[x][y] == 3) {
                        g2.setColor(Color.GREEN);
                    }else if (GRID_MAP[x][y] == 1) {
                        Color cl = new Color(113, 67, 27, 255);
                        if (dungeonGeneration.getRoom(x,y) != null &&
                                GRID_MAP[x][y] == 1 &&
                                !Objects.isNull(roomNames.get(dungeonGeneration.getRoom(x,y))) &&
                                roomNames.get(dungeonGeneration.getRoom(x,y)).equalsIgnoreCase("ROOM_RARE"))
                            cl = new Color(160,32,240, 255);
                        g2.setColor(cl);
                    } else g2.setColor(c1);
                }
                g2.fillRect(currx, curry, 14, 14);//18 perfect size
            }
        }
        g2.setColor(new Color(113, 67, 27, 255));
        g2.dispose();
        return image;
    }
}
