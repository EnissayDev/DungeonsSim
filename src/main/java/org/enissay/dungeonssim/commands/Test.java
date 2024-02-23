package org.enissay.dungeonssim.commands;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;


import java.util.*;
import java.util.stream.Collectors;

public class Test {

    private static int gridHeight = 6;
    private static int gridWidth = 6;
    private static int MIN_ROOM = 20;
    private static int MAX_ROOM = 30;
    private static int[][] GRID_MAP = new int[gridWidth][gridHeight]; // Grid map
    private static List<GridCell> rooms = new LinkedList<>();
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        generateDungeon(5, 5);
    }

    public Test(int gridHeight, int gridWidth, int minRoom, int maxRoom) {
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.MAX_ROOM = maxRoom;
        this.MIN_ROOM = minRoom;
        this.GRID_MAP = new int[gridWidth][gridHeight];
        generateDungeon(0, 0);
    }

    public int[][] getGRID_MAP() {
        return GRID_MAP;
    }

    public  List<GridCell> getRooms() {
        return rooms;
    }

    public static class GridCell {
        int X, Y;
        Direction direction;

        public GridCell(int x, int y, Direction direction) {
            X = x;
            Y = y;
            this.direction = direction;
        }

        public int getX() {
            return X;
        }

        public int getY() {
            return Y;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST;

        public Direction opposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case EAST:
                    return WEST;
                case SOUTH:
                    return NORTH;
                case WEST:
                    return EAST;
                default:
                    throw new IllegalStateException("Unexpected value: " + this);
            }
        }
    }

    private static void generateDungeon(int startX, int startY) {
        int[] directionX = {-1, 1, 0, 0};
        int[] directionY = {0, 0, -1, 1};

        int roomCount = 0; // Track the number of rooms generated
        int attempt = 0;
        int spawnCount = 0;
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
                    // If the new cell is not already part of the dungeon
                    GRID_MAP[newX][newY] = 1;
                    Direction direction;
                    // Determine the direction
                    if (newX > currentX) {
                        direction = Direction.EAST;
                    } else if (newX < currentX) {
                        direction = Direction.WEST;
                    } else if (newY > currentY) {
                        direction = Direction.SOUTH;
                    } else {
                        direction = Direction.NORTH;
                    }
                    //System.out.println("ADDED X:" + newX + " Y:" + newY);
                    rooms.add(new GridCell(newX, newY, direction));

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
        final int randMaxCol = RANDOM.nextInt(maxY - 1, maxY);
        final int randSX = getRandomRowInColumn(randCol, RANDOM);
        final int randBX = getRandomRowInColumn(randMaxCol, RANDOM);
        System.out.println("Found for spawn x: " + randSX + " y: " + randCol);
        System.out.println("Found for boss x: " + randBX + " y: " + randMaxCol);
            GRID_MAP[randSX][randCol] = 3;
            GRID_MAP[randBX][randMaxCol] = 2;
        System.out.println("Rooms generated: " + rooms.size());
        printGrid(GRID_MAP, rooms);

    }

    public static boolean find(int room) {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                return (GRID_MAP[x][y] == room);
            }
        }
        return false;
    }

    private static int getMinimumValidY() {
        //rooms.forEach(room -> System.out.println(room.X + " " + room.Y));
        return rooms.stream()
                .collect(Collectors.minBy(Comparator.comparing(GridCell::getY))).get().getY();
    }

    private static int getMaximumValidY() {
        //rooms.forEach(room -> System.out.println(room.X + " " + room.Y));
        return rooms.stream()
                .collect(Collectors.maxBy(Comparator.comparing(GridCell::getY))).get().getY();
    }
    private static int getRandomRowInColumn(int columnY, Random random) {
        //System.out.println(getRoomsInColumn(columnY).size() + " for Y: " + columnY);
        List<Integer> availableRows = new ArrayList<>();
        for (GridCell cell : getRoomsInColumn(columnY)) {
            if (cell.getY() == columnY) {
                //System.out.println("Found " + cell.getX() + " for y: " + columnY);
                availableRows.add(cell.getX());
            }
        }
        // Select a random row from the available rows
        return availableRows.size() > 0 ? availableRows.get(random.nextInt(0, availableRows.size())) : 0;
    }

    public static List<GridCell> getRoomsInColumn(final int columnY) {
        List<GridCell> gridCells = new ArrayList<>();
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

    private static void resetGrid(int[][] GRID_MAP) {
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                GRID_MAP[i][j] = 0;
            }
        }
    }

    private static void printGrid(int[][] GRID_MAP, List<GridCell> rooms) {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                String c = " ";
                if (GRID_MAP[x][y] == 1) {
                    Direction roomDirection = findRoomDirection(rooms, x, y);
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

    private static Direction findRoomDirection(List<GridCell> rooms, int x, int y) {
        for (GridCell room : rooms) {
            if (room.getX() == x && room.getY() == y) {
                return room.getDirection();
            }
        }
        return null;
    }


    /*public static void main(String[] args)
    {
        DelaunayTriangulator delaunayTriangulator = null;
        try {
            // Create a set of 2D points
            Vector<Vector2D> pointSet = new Vector<>();
            pointSet.add(new Vector2D(0, 0));
            pointSet.add(new Vector2D(1, 1));
            pointSet.add(new Vector2D(2, 5));
            pointSet.add(new Vector2D(3, 3));
            pointSet.add(new Vector2D(5, 4));
            pointSet.add(new Vector2D(10, 2));

            // Perform Delaunay triangulation
            delaunayTriangulator = new DelaunayTriangulator(pointSet);
            delaunayTriangulator.triangulate();

            // Get the resulting triangles
            List<Triangle2D> triangleSoup = delaunayTriangulator.getTriangles();
            triangleSoup.forEach(tr -> {
                System.out.println(tr.a + " - " + tr.b);
            });

            // Convert the resulting triangles to a graph representation
            Graph graph = new Graph();
            for (Triangle2D triangle : triangleSoup) {
                double distanceAB = Math.sqrt(Math.pow(triangle.a.x - triangle.b.x, 2) + Math.pow(triangle.a.y - triangle.b.y, 2));
                double distanceBC = Math.sqrt(Math.pow(triangle.b.x - triangle.c.x, 2) + Math.pow(triangle.b.y - triangle.c.y, 2));
                double distanceCA = Math.sqrt(Math.pow(triangle.c.x - triangle.a.x, 2) + Math.pow(triangle.c.y - triangle.a.y, 2));

                graph.addEdge(triangle.a, triangle.b, distanceAB);
                graph.addEdge(triangle.b, triangle.c, distanceBC);
                graph.addEdge(triangle.c, triangle.a, distanceCA);
            }

            // Find the minimum spanning tree
            System.out.println("Minimum spanning tree");
            List<Vector2D> minimumSpanningTree = MinimumSpanningTree.primMST(graph);
            minimumSpanningTree.forEach(min -> System.out.println(min));

            //minimumSpanningTree.forEach(min -> System.out.println(min.a + " - " + min.b));

        } catch (NotEnoughPointsException e) {
        }
    }
    static class Graph {
        Map<Vector2D, List<Vector2D>> adjacencyList;
        private Map<Vector2D, Map<Vector2D, Double>> edgeWeights;

        public Graph() {
            this.adjacencyList = new HashMap<>();
            this.edgeWeights = new HashMap<>();
        }

        public void addVertex(Vector2D vertex) {
            if (!adjacencyList.containsKey(vertex)) {
                adjacencyList.put(vertex, new ArrayList<>());
                edgeWeights.put(vertex, new HashMap<>());
            }
        }

        public void addEdge(Vector2D source, Vector2D destination, double weight) {
            addVertex(source);
            addVertex(destination);
            adjacencyList.get(source).add(destination);
            adjacencyList.get(destination).add(source); // Assuming undirected graph
            edgeWeights.get(source).put(destination, weight);
            edgeWeights.get(destination).put(source, weight); // Assuming undirected graph
        }

        public List<Vector2D> getNeighbors(Vector2D vertex) {
            return adjacencyList.getOrDefault(vertex, Collections.emptyList());
        }

        public double getWeight(Vector2D source, Vector2D destination) {
            return edgeWeights.getOrDefault(source, Collections.emptyMap()).getOrDefault(destination, Double.POSITIVE_INFINITY);
        }
    }
    class MinimumSpanningTree {
        public static List<Vector2D> primMST(Graph graph) {
            List<Vector2D> mst = new ArrayList<>();
            Set<Vector2D> visited = new HashSet<>();
            PriorityQueue<Vector2D> minHeap = new PriorityQueue<>(Comparator.comparingDouble(vertex -> graph.getWeight(vertex, visited.stream().findFirst().orElse(null))));

            // Start Prim's algorithm from an arbitrary vertex (or multiple starting vertices)
            Vector2D startVertex = graph.adjacencyList.keySet().iterator().next();
            visited.add(startVertex);
            minHeap.addAll(graph.getNeighbors(startVertex));

            while (!minHeap.isEmpty()) {
                Vector2D minVertex = minHeap.poll();
                if (!visited.contains(minVertex)) {
                    visited.add(minVertex);
                    mst.add(minVertex);
                    minHeap.addAll(graph.getNeighbors(minVertex));
                }
            }

            return mst;
        }
    }*/
}
