package org.enissay.dungeonssim;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.enissay.dungeonssim.commands.GenerateCommand;
import org.enissay.dungeonssim.commands.TestCommand;
import org.enissay.dungeonssim.commands.dungeon.DungeonCommand;
import org.enissay.dungeonssim.commands.dungeonloc.DungeonLocationsCommand;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonListener;
import org.enissay.dungeonssim.dungeon.*;
import org.enissay.dungeonssim.dungeon.templates.BossRoom;
import org.enissay.dungeonssim.dungeon.templates.NormalRoom;
import org.enissay.dungeonssim.dungeon.templates.SpawnRoom;
import org.enissay.dungeonssim.handlers.DungeonHandler;

public class DungeonsSim extends JavaPlugin {

    private static DungeonsSim instance;

    @Override
    public void onEnable() {
        instance = this;

        EventManager.init(this);
        Bukkit.getPluginManager().registerEvents(new TempDungeonListener(), this);

        DungeonHandler.register(new SpawnRoom(), new NormalRoom(), new BossRoom());

        LiteCommandsBukkit.builder("Dungeons", this)
                .commands(new GenerateCommand(), new DungeonLocationsCommand(), new TestCommand(), new DungeonCommand())
                .build();
        /*EventManager.on(AsyncPlayerChatEvent.class, (event -> {
            final Player player = event.getPlayer();
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            if (dungeon != null) {
                dungeon.getRooms().forEach(room -> {
                    if (room.getWatchers().size() > 0) {
                        List<String> names = new ArrayList<>();
                        room.getWatchers().forEach(uuid -> {
                            names.add(Bukkit.getPlayer(uuid).getName());
                        });
                        player.sendMessage("Players in " + room.getTemplate().getName() + " : " + String.join(", ", names));
                    }
                });
            }
            final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(player.getLocation());
            if (dungeonRoom != null && dungeonRoom.getCuboid().isIn(player.getLocation()))
                player.sendMessage("You're in: " + dungeonRoom.getRoomName() + " template: " + dungeonRoom.getTemplate().getName() + " dungeonID: " + dungeonRoom.getDungeon().getID());        }));*/

    }

    @Override
    public void onDisable() {
        if (DungeonHandler.getDungeons().size() > 0) {
            DungeonHandler.getDungeons().forEach(dungeon -> {
                //Bukkit.broadcastMessage("#" + dungeon.getID() + " " + RoomPasting.getSessionsFor(dungeon).size());
                RoomPasting.getSessionsFor(dungeon).forEach(editSession -> {
                    try (EditSession newSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")))) {
                        editSession.undo(newSession);
                    }
                });
            });
        }
    }

    public static DungeonsSim getInstance() {
        return instance;
    }
}
