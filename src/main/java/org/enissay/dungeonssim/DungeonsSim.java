package org.enissay.dungeonssim;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.jeff_media.armorequipevent.ArmorEquipEvent;
import com.samjakob.spigui.SpiGUI;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteCommandsBukkit;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.enissay.dungeonssim.commands.GenerateCommand;
import org.enissay.dungeonssim.commands.TestCommand;
import org.enissay.dungeonssim.commands.admin.*;
import org.enissay.dungeonssim.commands.args.DungeonDifficultyArgument;
import org.enissay.dungeonssim.commands.args.OfflinePlayerArgument;
import org.enissay.dungeonssim.commands.args.PartyMemberArgument;
import org.enissay.dungeonssim.commands.args.RankArgument;
import org.enissay.dungeonssim.commands.dungeon.ClassCommand;
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
import org.enissay.dungeonssim.dungeon.templates.puzzle.impl.SoulCollecting;
import org.enissay.dungeonssim.entities.hostile.boss.impl.abilities.LaserBeamAbility;
import org.enissay.dungeonssim.gui.GUIListener;
import org.enissay.dungeonssim.gui.GUIManager;
import org.enissay.dungeonssim.handlers.*;
import org.enissay.dungeonssim.listeners.*;
import org.enissay.dungeonssim.profiles.Rank;

import java.io.IOException;

public class DungeonsSim extends JavaPlugin {

    private BukkitAudiences adventure;
    private static DungeonsSim instance;
    private LiteCommands liteCommands;
    private SpiGUI spiGUI;
    private Location spawnLocation;
    private ProtocolManager manager;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        instance = this;
        spiGUI = new SpiGUI(this);
        adventure = BukkitAudiences.create(this);
        manager = ProtocolLibrary.getProtocolManager();
        guiManager = new GUIManager();
        //spawnLocation = new Location(Bukkit.getWorld("world"), 18007, 89, 18863, -180, 0);
        spawnLocation = new Location(Bukkit.getWorld("world"), -24.5, 128, -15.5, 0, 0);

        Bukkit.getWorld("world").setSpawnLocation(spawnLocation);

        NPCLib.getInstance().registerPlugin(this);
        EventManager.init(this);
        ScoreboardHandler.init();
        ProfilesHandler.init();
        EntitiesHandler.init();
        CustomItemsHandler.init();

        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new SafeZoneListeners(), this);
        Bukkit.getPluginManager().registerEvents(new TempDungeonListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new DungeonListener(), this);
        //Bukkit.getPluginManager().registerEvents(new EventAnalyser(), this);
        ArmorEquipEvent.registerListener(this);

        DungeonHandler.register(new SpawnRoom(), new NormalRoom(), new BossRoom(), new PuzzleRoom());
        PuzzleHandler.register(new PuzzleCubes(), new SoulCollecting());

        DungeonParser.registerClipboardForRooms();

        this.liteCommands = LiteCommandsBukkit.builder("Dungeons", this)
                .argument(OfflinePlayer.class, new OfflinePlayerArgument(this.getServer()))
                .argument(PartyMember.class, new PartyMemberArgument(this.getServer()))
                .argument(DungeonDifficulty.class, new DungeonDifficultyArgument())
                .argument(Rank.class, new RankArgument())
                .commands(new GenerateCommand(), new DungeonLocationsCommand(), new TestCommand(), new DungeonCommand(), new PartyCommands(),
                        new RankCommand(), new BuilderCommand(), new MobCommand(), new ItemsCommand(), new ClassCommand(),
                        new LevelCommand())
                .build();

        Bukkit.getWorld("world").setDifficulty(Difficulty.EASY);
        LobbyHandler.init();
    }

    @Override
    public void onDisable() {
        //HologramAPI.getHologram().removeAll();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.liteCommands.unregister();
        LobbyHandler.getLobbyNpcs().forEach(NPC.Global::destroy);
        LobbyHandler.getMobs().forEach(m -> {
            m.getBukkitEntity().remove();
        });

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

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public ProtocolManager getManager() {
        return manager;
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
