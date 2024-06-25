package org.enissay.dungeonssim.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.enissay.dungeonssim.DungeonsSim;

public abstract class RepeatingTask implements Runnable {
    private final int taskId;

    /**
     * Repeating task
     * @param arg1 Delay
     * @param arg2 Period
     */
    public RepeatingTask(int arg1, int arg2) {
        Plugin plugin = DungeonsSim.getInstance();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, arg1, arg2);
    }

    public int getTaskId() {
        return taskId;
    }

    /**
     * Cancel the task
     */
    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}