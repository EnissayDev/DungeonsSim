package org.enissay.dungeonssim;

import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.enissay.dungeonssim.commands.GenerateCommand;
import org.enissay.dungeonssim.commands.TestCommand;
import org.enissay.dungeonssim.commands.dungeonloc.DungeonLocationsCommand;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonListener;
import org.enissay.dungeonssim.dungeon.templates.BossRoom;
import org.enissay.dungeonssim.dungeon.templates.NormalRoom;
import org.enissay.dungeonssim.dungeon.templates.SpawnRoom;
import org.enissay.dungeonssim.handlers.DungeonHandler;

public class DungeonsSim extends JavaPlugin {

    private static DungeonsSim instance;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new TempDungeonListener(), this);

        DungeonHandler.register(new SpawnRoom(), new NormalRoom(), new BossRoom());

        LiteCommandsBukkit.builder("Dungeons", this)
                .commands(new GenerateCommand(), new DungeonLocationsCommand(), new TestCommand())
                .build();
    }

    public static DungeonsSim getInstance() {
        return instance;
    }
}
