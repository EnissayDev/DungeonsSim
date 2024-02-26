package org.enissay.dungeonssim.dungeon;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventManager {
    private static Plugin plugin;
    public static void init(Plugin plugin) {
        EventManager.plugin = plugin;
    }
    public static <T> void on(Class<T> event, EventCallback<T> callback) {
        Preconditions.checkNotNull(event);
        Preconditions.checkNotNull(callback);
        //check if event extends org.bukkit.event.Event
        if (!Event.class.isAssignableFrom(event)) {
            throw new IllegalArgumentException("Event class must extend org.bukkit.event.Event");
        }
        Bukkit.getPluginManager().registerEvent((Class<? extends Event>) event, new Listener() {}, EventPriority.HIGHEST, (listener, event1) -> {
            callback.onEvent((T) event1);
        }, plugin);
    }
    public static void register(Listener listener) {
        if (plugin instanceof JavaPlugin)
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        else{
            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)){
                    Class<? extends Event> event = (Class<? extends Event>) method.getParameterTypes()[0];
                    on(event,(e)->{
                        try {
                            method.invoke(listener,e);
                        } catch (IllegalAccessException | InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        }
    }
    public interface EventCallback<T>{
        void onEvent(T event);
    }
}