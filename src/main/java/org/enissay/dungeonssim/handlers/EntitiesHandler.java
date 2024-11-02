package org.enissay.dungeonssim.handlers;

import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.entities.hostile.CustomHostileMob;
import org.enissay.dungeonssim.entities.passive.CustomNonHostileMob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class EntitiesHandler {

    private static LinkedList<CustomMob> customMobs = new LinkedList<>();
    private static LinkedList<Class<? extends CustomMob>> customMobsClasses = new LinkedList<>();

    public static void addEntity(CustomMob m) {
        if (!customMobs.contains(m) || customMobs.stream().filter(cm -> cm.getEntityCustomName().equals(m.getEntityCustomName())).findAny().orElse(null) == null)
            customMobs.add(m);
    }

    public static void addEntityClass(Class<? extends CustomMob> m) {
        if (!customMobsClasses.contains(m) || customMobsClasses.stream().filter(cm -> cm.getName().equals(m.getName())).findAny().orElse(null) == null)
            customMobsClasses.add(m);
    }

    public static void addEntityClass(Class<? extends CustomMob>... mbs) {
        for (Class<? extends CustomMob> mb : mbs) {
            addEntityClass(mb);
        }
    }

    public static void init() {
        addEntityClass(CustomHostileMob.class, CustomNonHostileMob.class);
    }

    public static CustomMob getCMFromNMSEntity(Entity entity) {
        return customMobs.stream()
                .filter(e -> e.getNMSEntity().equals(entity))
                .findAny().orElse(null);
    }

    public static boolean exists(org.bukkit.entity.Entity entity) {
        boolean exists = false;
        for (CustomMob customMob : getCustomMobs()) {
            exists = customMob.getNMSEntity().getBukkitEntity().equals(entity);
        }
        return exists;
    }

    public static boolean existsNMS(Entity entity) {
        return getCustomMobs().stream()
                .map(CustomMob::getNMSEntity)
                .collect(Collectors.toList())
                .contains(entity);
    }

    public static CustomMob getCMFromEntity(org.bukkit.entity.Entity entity) {
        return customMobs.stream()
                .filter(e -> e.getNMSEntity().getBukkitEntity().equals(entity))
                .findAny().orElse(null);
    }

    public static LinkedList<Class<? extends CustomMob>> getCustomMobsClasses() {
        return customMobsClasses;
    }

    public static LinkedList<CustomMob> getCustomMobs() {
        return customMobs;
    }

    // New method to get the class from the entity custom name
    public static Class<? extends CustomMob> getClassFromEntityCustomName(String entityCustomName) {
        for (Class<? extends CustomMob> customMobClass : customMobsClasses) {
            Method[] methods = customMobClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().contains("getEntityCustomName")) {
                    try {
                        String name = (String) method.invoke(null);
                        if (name.equals(entityCustomName)) {
                            return customMobClass;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }
}
