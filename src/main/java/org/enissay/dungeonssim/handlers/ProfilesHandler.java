package org.enissay.dungeonssim.handlers;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;
import org.enissay.dungeonssim.profiles.event.PlayerLevelUpEvent;

import java.io.*;
import java.time.Instant;
import java.util.*;

public class ProfilesHandler {

    private static List<DungeonPlayer> profiles = new ArrayList<DungeonPlayer>();

    public static void init() {
        try {
            loadProfiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DungeonPlayer createProfile(Player p, Rank rank){

        final DungeonPlayer minefortPlayer = new DungeonPlayer(p.getUniqueId().toString(), p.getName(), rank, Date.from(Instant.now()), null, 0);
        profiles.add(minefortPlayer);

        return minefortPlayer;
    }

    public static void deleteProfile(String id) throws IOException {
        for (DungeonPlayer profile : profiles) {
            if (profile.getID().equalsIgnoreCase(id)) {
                profiles.remove(profile);
                break;
            }
        }
        saveProfiles();
    }

    public static DungeonPlayer findProfile(String id){
        for (DungeonPlayer profile : profiles) {
            if (profile.getID().equalsIgnoreCase(id)) {
                return profile;
            }
        }
        return null;
    }

    public static DungeonPlayer findProfileByName(String name){
        for (DungeonPlayer profile : profiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }

    public static void giveEXP(String id, final long exp) {
        DungeonPlayer profile = findProfile(id);
        final int oldLevel = profile.getLevel();
        if (profile != null) {
            profile.setExp(profile.getExp()+exp);
            updateProfile(id, profile);
            final int newLevel = profile.getLevel();
            if (newLevel > oldLevel)
                Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(Bukkit.getPlayer(UUID.fromString(id)), findProfile(id), newLevel, oldLevel));
        }
    }

    public static void setEXP(String id, final long exp) {
        DungeonPlayer profile = findProfile(id);
        final int oldLevel = profile.getLevel();
        if (profile != null) {
            profile.setExp(exp);
            updateProfile(id, profile);
            final int newLevel = profile.getLevel();
            if (newLevel > oldLevel)
                Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(Bukkit.getPlayer(UUID.fromString(id)), findProfile(id), newLevel, oldLevel));
        }
    }
    public static DungeonPlayer updateProfile(String id, DungeonPlayer newProfile){
        for (DungeonPlayer profile : profiles) {
            if (profile.getID().equalsIgnoreCase(id)) {
                profile.setName(newProfile.getName());
                profile.setRank(newProfile.getRank());
                profile.setJoinDate(newProfile.getJoinDate());
                profile.setLastOnline(newProfile.getLastOnline());
                profile.setCoins(newProfile.getCoins());
                profile.setPlayerClass(newProfile.getPlayerClass());
                profile.setExp(newProfile.getExp());
            }
        }
        ScoreboardHandler.update();
        return null;
    }

    public static DungeonPlayer updateProfileByName(String name, DungeonPlayer newProfile){
        for (DungeonPlayer profile : profiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                profile.setName(newProfile.getName());
                profile.setRank(newProfile.getRank());
                profile.setJoinDate(newProfile.getJoinDate());
                profile.setLastOnline(newProfile.getLastOnline());
                profile.setCoins(newProfile.getCoins());
                profile.setPlayerClass(newProfile.getPlayerClass());
                profile.setExp(newProfile.getExp());
            }
        }
        ScoreboardHandler.update();
        return null;
    }

    public static List<DungeonPlayer> findAllProfiles(){
        return profiles;
    }

    public static void loadProfiles() throws IOException {

        Gson gson = new Gson();
        File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() + "/profiles.json");
        if (file.exists()){
            Reader reader = new FileReader(file);
            DungeonPlayer[] n = gson.fromJson(reader, DungeonPlayer[].class);
            profiles = new ArrayList<>(Arrays.asList(n));
            System.out.println("Profiles loaded.");
        }
    }

    public static void saveProfiles() throws IOException {

        Gson gson = new Gson();
        System.out.println(DungeonsSim.getInstance().getDataFolder().getAbsolutePath());
        File file = new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() + "/profiles.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        profiles.forEach(profile -> {
            if (profile.getJoinDate() == null) profile.setJoinDate(Date.from(Instant.now()));
            if (profile.getLastOnline() == null) profile.setLastOnline(Date.from(Instant.now()));
            if (Objects.isNull(profile.getCoins())) profile.setCoins(0);
            if (profile.getRank() == null) profile.setRank(Rank.PLAYER);
        });
        gson.toJson(profiles, writer);
        writer.flush();
        writer.close();
        System.out.println("Profiles saved.");

    }
}