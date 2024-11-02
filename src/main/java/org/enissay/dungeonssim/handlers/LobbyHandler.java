package org.enissay.dungeonssim.handlers;

import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.party.DungeonPartyStatus;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.passive.Dummy;
import org.enissay.dungeonssim.utils.Mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LobbyHandler {
    private static List<AbstractCustomMob> mobs;
    private static List<NPC.Global> lobbyNpcs;
    private static World world = Bukkit.getWorld("world");
    private static Location createParty = new Location(world, -27.5, 128, -11.5, -150f, 0);
    private static Location showPartys = new Location(world, -19.5, 128, -7.5, 150f, 0f);
    private static Location startDungeon = new Location(world, -24.5, 128, -1.5, -180f, 0f);
    private static Location fastSetup = new Location(world, -10.5, 128, 7.5, 150f, 0f);
    private static Location dummyLocation = new Location(world, -35.5, 128, 0.5, -90f, 0f);

    private static final String USERNAME_PATTERN =
            "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";

    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);

    public static boolean isValid(final String username) {
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public static void init() {
        lobbyNpcs = new ArrayList<>();
        mobs = new ArrayList<>();

        NPC.Global npcCreateParty = NPCLib.getInstance().generateGlobalNPC(DungeonsSim.getInstance(), "createParty", createParty);
        npcCreateParty.setText("&6Create Party", "&7Interact");
        npcCreateParty.setMineSkin("bc8821e6545c4847bfb57302a99b2691", true, (result) -> {});
        npcCreateParty.addCustomClickAction((npc, clicker) -> {
            new AnvilGUI.Builder()
                    /*.onClose(stateSnapshot -> {
                        stateSnapshot.getPlayer().sendMessage("You closed the inventory.");
                    })*/
                    .onClick((slot, stateSnapshot) -> {
                        if(slot != AnvilGUI.Slot.OUTPUT) {
                            return Collections.emptyList();
                        }
                        if(isValid(stateSnapshot.getText())) {
                            clicker.performCommand("dp create " + stateSnapshot.getText());
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        } else {
                            return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Try again"));
                        }
                    })
                    //.preventClose()                                                    //prevents the inventory from being closed
                    .text("Name")                              //sets the text the GUI should start with
                    .title("Party Name")                                       //set the title of the GUI (only works in 1.14+)
                    .plugin(DungeonsSim.getInstance())                                          //set the plugin instance
                    .open(clicker);
        });
        NPC.Global npcShowPartys = NPCLib.getInstance().generateGlobalNPC(DungeonsSim.getInstance(), "showPartys", showPartys);
        npcShowPartys.setMineSkin("c107f1dc634b41b7af4651da4f913e0b", true, (result) -> {});
        npcShowPartys.setText("&c" + PartyHandler.getDungeonPartys().size() + "&f Partys available",
                "&a" + PartyHandler.getDungeonPartys().stream().filter(DungeonParty::isPublic).count() + "&f Open partys",
                "&6" + PartyHandler.getDungeonPartys().stream().filter(dp -> {
                    return dp.getStatus() == DungeonPartyStatus.PLAYING;
                }).count() + "&f Partys in a Dungeon",
                "", "&6Show Partys", "&7Interact");
        npcShowPartys.addCustomClickAction((npc, clicker) -> {
            clicker.performCommand("dp gui");
        });
        new BukkitRunnable() {

            @Override
            public void run() {
                if (npcShowPartys != null) {
                    npcShowPartys.setText("&c" + PartyHandler.getDungeonPartys().size() + "&f Partys available",
                            "&a" + PartyHandler.getDungeonPartys().stream().filter(DungeonParty::isPublic).count() + "&f Open partys",
                            "&6" + PartyHandler.getDungeonPartys().stream().filter(dp -> {
                                return dp.getStatus() == DungeonPartyStatus.PLAYING;
                            }).count() + "&f Partys in a Dungeon",
                            "", "&6Show Partys", "&7Interact");
                    npcShowPartys.simpleUpdateText();
                }else cancel();
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, 4 * 20L);

        NPC.Global npcStartDungeon = NPCLib.getInstance().generateGlobalNPC(DungeonsSim.getInstance(), "startDungeon", startDungeon);
        npcStartDungeon.setMineSkin("ce0b3e0812ed4801b1fd948dac67cd83", true, (result) -> {});
        npcStartDungeon.setText("&7(Needs to be the host of a Party)", "&c&lMULTI", "&cStart Dungeon", "&7Interact");
        npcStartDungeon.addCustomClickAction((npc, clicker) -> {
            clicker.performCommand("dp start");
        });

        NPC.Global npcSetupDungeon = NPCLib.getInstance().generateGlobalNPC(DungeonsSim.getInstance(), "fastSetup", fastSetup);
        npcSetupDungeon.setMineSkin("d9fd9b42786d46cb9436e14e06ed70f8", true, (result) -> {});
        npcSetupDungeon.setText("&c&lSOLO","&aFast Setup", "&7Interact");
        npcSetupDungeon.addCustomClickAction((npc, clicker) -> {
            clicker.performCommand("dp create dungeon_" +  Mode.getString(5, Mode.ALPHANUMERIC));
            clicker.performCommand("dp start");
        });

        lobbyNpcs.add(npcSetupDungeon);
        lobbyNpcs.add(npcStartDungeon);
        lobbyNpcs.add(npcShowPartys);
        lobbyNpcs.add(npcCreateParty);

        lobbyNpcs.forEach(h -> {
            h.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);
            h.setInteractCooldown(2);
            h.setTabListVisibility(NPC.TabListVisibility.NEVER);
            h.setHideDistance(1000);
            h.setCollidable(false);
        });
        //lobbyNpcs.forEach(NPC.Global::createAllPlayers);
        lobbyNpcs.forEach(NPC.Global::update);
        lobbyNpcs.forEach(NPC.Global::forceUpdate);

        //MOBS
        mobs.add(new Dummy(dummyLocation, 1, 10000000000.0));
    }

    public static List<AbstractCustomMob> getMobs() {
        return mobs;
    }

    public static List<NPC.Global> getLobbyNpcs() {
        return lobbyNpcs;
    }
}
