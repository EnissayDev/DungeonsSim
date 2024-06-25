package org.enissay.dungeonssim.commands.dungeonloc;

import org.enissay.dungeonssim.dungeon.templates.RoomLocation;

import java.util.HashMap;
import java.util.Map;

public class TempDungeonBuilds {

    private java.util.Map<String, RoomLocation> roomLocations = new HashMap<>();
    private String name;
    private String templateName;
    private int minY;
    private double chance;

    public TempDungeonBuilds(String templateName, String name, int minY, double chance) {
        this.name = name;
        this.templateName = templateName;
        this.minY = minY;
        this.chance = chance;
    }

    public Map<String, RoomLocation> getRoomLocations() {
        return roomLocations;
    }

    public void putLocation(final String locationName, final RoomLocation location) {
        roomLocations.put(locationName, location);
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public RoomLocation getLocation1() {
        return roomLocations.get("location_1");
    }

    public RoomLocation getLocation2() {
        return roomLocations.get("location_2");
    }

    public String getTemplateName() {
        return templateName;
    }

    public double getChance() {
        return chance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
