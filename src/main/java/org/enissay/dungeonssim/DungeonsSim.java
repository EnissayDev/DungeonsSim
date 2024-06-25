package org.enissay.dungeonssim;

import com.samjakob.spigui.SpiGUI;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.enissay.dungeonssim.commands.GenerateCommand;
import org.enissay.dungeonssim.commands.TestCommand;
import org.enissay.dungeonssim.commands.admin.BuilderCommand;
import org.enissay.dungeonssim.commands.admin.MobCommand;
import org.enissay.dungeonssim.commands.admin.RankCommand;
import org.enissay.dungeonssim.commands.args.DungeonDifficultyArgument;
import org.enissay.dungeonssim.commands.args.OfflinePlayerArgument;
import org.enissay.dungeonssim.commands.args.PartyMemberArgument;
import org.enissay.dungeonssim.commands.args.RankArgument;
import org.enissay.dungeonssim.commands.dungeon.DungeonCommand;
import org.enissay.dungeonssim.commands.dungeonloc.DungeonLocationsCommand;
import org.enissay.dungeonssim.commands.dungeonloc.TempDungeonListener;
import org.enissay.dungeonssim.commands.party.PartyCommands;
import org.enissay.dungeonssim.dungeon.*;
import org.enissay.dungeonssim.dungeon.party.PartyMember;
import org.enissay.dungeonssim.dungeon.system.DungeonDifficulty;
import org.enissay.dungeonssim.dungeon.system.DungeonParser;
import org.enissay.dungeonssim.dungeon.templates.impl.BossRoom;
import org.enissay.dungeonssim.dungeon.templates.impl.NormalRoom;
import org.enissay.dungeonssim.dungeon.templates.impl.PuzzleRoom;
import org.enissay.dungeonssim.dungeon.templates.impl.SpawnRoom;
import org.enissay.dungeonssim.dungeon.templates.puzzle.impl.PuzzleCubes;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.handlers.PuzzleHandler;
import org.enissay.dungeonssim.handlers.ScoreboardHandler;
import org.enissay.dungeonssim.listeners.ChatListener;
import org.enissay.dungeonssim.listeners.SafeZoneListeners;
import org.enissay.dungeonssim.profiles.Rank;

import java.io.IOException;

public class DungeonsSim extends JavaPlugin {

    private static DungeonsSim instance;
    private LiteCommands liteCommands;
    private SpiGUI spiGUI;
    private Location spawnLocation;

    @Override
    public void onEnable() {
        instance = this;
        spiGUI = new SpiGUI(this);
        spawnLocation = new Location(Bukkit.getWorld("world"), 18007, 89, 18863, -180, 0);
        Bukkit.getWorld("world").setSpawnLocation(spawnLocation);

        EventManager.init(this);
        ScoreboardHandler.init();
        ProfilesHandler.init();

        Bukkit.getPluginManager().registerEvents(new SafeZoneListeners(), this);
        Bukkit.getPluginManager().registerEvents(new TempDungeonListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        DungeonHandler.register(new SpawnRoom(), new NormalRoom(), new BossRoom(), new PuzzleRoom());
        PuzzleHandler.register(new PuzzleCubes());

        DungeonParser.registerClipboardForRooms();

        this.liteCommands = LiteCommandsBukkit.builder("Dungeons", this)
                .argument(OfflinePlayer.class, new OfflinePlayerArgument(this.getServer()))
                .argument(PartyMember.class, new PartyMemberArgument(this.getServer()))
                .argument(DungeonDifficulty.class, new DungeonDifficultyArgument())
                .argument(Rank.class, new RankArgument())
                .commands(new GenerateCommand(), new DungeonLocationsCommand(), new TestCommand(), new DungeonCommand(), new PartyCommands(),
                        new RankCommand(), new BuilderCommand(), new MobCommand())
                .build();

        Bukkit.getWorld("world").setDifficulty(Difficulty.PEACEFUL);

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
        this.liteCommands.unregister();

        DungeonParser.getClipboards().clear();
        if (DungeonHandler.getDungeons().size() > 0) {
            DungeonHandler.getDungeons().forEach(dungeon -> {
                //Bukkit.broadcastMessage("#" + dungeon.getID() + " " + RoomPasting.getSessionsFor(dungeon).size());
                DungeonHandler.removeDungeon(dungeon);
                /*RoomPasting.getSessionsFor(dungeon).forEach(editSession -> {
                    try (EditSession newSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("world")))) {
                        editSession.undo(newSession);
                    }
                });*/
            });
        }
        try {
            ProfilesHandler.saveProfiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SpiGUI getSpiGUI() {
        return spiGUI;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public static DungeonsSim getInstance() {
        return instance;
    }
}
