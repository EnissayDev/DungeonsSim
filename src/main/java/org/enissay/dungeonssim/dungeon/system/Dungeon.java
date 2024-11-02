package org.enissay.dungeonssim.dungeon.system;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.commands.DecentCommandException;
import eu.decentsoftware.holograms.api.holograms.DisableCause;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.plugin.Validator;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.templates.puzzle.Puzzle;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.hostile.boss.AbstractBossMob;
import org.enissay.dungeonssim.utils.Cuboid;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Dungeon {

    private int ID;
    private boolean hasBossKey;
    private List<UUID> players;
    private LinkedList<DungeonRoom> rooms;
    private Cuboid cuboid;
    private DungeonDifficulty dungeonDifficulty;
    private DungeonGeneration dungeonGeneration;
    private LinkedList<DungeonRoom> roomsOpened;
    private Instant timeStarted;
    private LinkedList<Hologram> holograms;
    private LinkedList<Puzzle> completedPuzzles;
    private LinkedList<Entity> mobs;
    private LinkedList<NPC.Personal> npcs;
    private Map<UUID, Integer> deaths;
    private Map<UUID, Double> damageCounter;
    private int maxDeaths;
    private RadioSongPlayer radioSongPlayer;

    public Dungeon(int ID, List<UUID> players, DungeonDifficulty dungeonDifficulty, Instant timeStarted, int maxDeaths) {
        this.ID = ID;
        this.hasBossKey = false;
        this.players = players;
        this.rooms = new LinkedList<>();
        this.holograms = new LinkedList<>();
        this.dungeonDifficulty = dungeonDifficulty;
        this.maxDeaths = maxDeaths;
        this.roomsOpened = new LinkedList<>();
        this.completedPuzzles = new LinkedList<>();
        this.mobs = new LinkedList<>();
        this.npcs = new LinkedList<>();
        this.deaths = new HashMap<>();
        this.damageCounter = new HashMap<>();
        this.timeStarted = timeStarted;
    }

    public void addHologram(Hologram hologram) {
        if (!holograms.contains(hologram)) {
            holograms.add(hologram);
        }
    }

    public void addCustomMob(Entity acm) {
        if (!mobs.contains(acm)) {
            mobs.add(acm);
        }
    }

    public void addNPC(NPC.Personal npc) {
        if (!npcs.contains(npc)) {
            npcs.add(npc);
        }
    }

    public void removeCustomMob(Entity acm) {
        if (acm == null) return;
        if (mobs.contains(acm)) {
            acm.remove(Entity.RemovalReason.KILLED);
            //acm.kill();
            mobs.remove(acm);
        }
    }

    public void removeHologram(Hologram hologram) {
        if(hologram == null)
            return;

        String name = hologram.getName();
        //hologram.delete();
        DHAPI.removeHologram(name);

        //if (holograms.contains(hologram)) holograms.remove(hologram);
    }

    public void removeNPC(NPC.Personal npc) {
        if (npc == null) return;
        if (npcs.contains(npc)) {
            NPCLib.getInstance().removePersonalNPC(npc);
            npcs.remove(npc);
        }
    }

    public void removeBossbar() {
        mobs.forEach(m -> {
            if (m instanceof AbstractBossMob abm)
                abm.getBossBar().getPlayers().forEach(pl -> abm.getBossBar().removePlayer(pl));
        });
    }

    public void removeAllMobs() {
        if (getAliveMobs().size() > 0) getAliveMobs().stream().filter(m -> !Objects.isNull(m)).forEach(this::removeCustomMob);
    }

    public void removeAllHolograms() {
        if (holograms.size() > 0) holograms.forEach(this::removeHologram);
    }

    public void removeAllNPCs() {
        if (npcs.size() > 0) npcs.forEach(this::removeNPC);
    }

    public boolean hasBossKey() {
        return hasBossKey;
    }

    public void giveBossKey() {
        this.hasBossKey = true;
    }

    public void addDeath(final UUID uuid) {
        if (deaths.containsKey(uuid) || deaths.get(uuid) != null) {
            deaths.put(uuid, getDeathsOf(uuid) + 1);
        }else deaths.put(uuid, 1);
    }

    public void addDamage(final UUID uuid, final double damage) {
        if (damageCounter.containsKey(uuid) || damageCounter.get(uuid) != null) {
            damageCounter.put(uuid, getTotalDamageOf(uuid) + damage);
        }else damageCounter.put(uuid, damage);
    }

    public void playSong(final String songName) {
        Song song = NBSDecoder.parse(new File(DungeonsSim.getInstance().getDataFolder().getAbsolutePath() +
                "/songs/" + songName + ".nbs"));
        if (this.radioSongPlayer != null && this.radioSongPlayer.isPlaying()) this.radioSongPlayer.setPlaying(false);
        this.radioSongPlayer = new RadioSongPlayer(song);

        radioSongPlayer.setAutoDestroy(true);
        //a.setRepeatMode(RepeatMode.ONE);
        getPlayers().forEach(uuid ->
            radioSongPlayer.addPlayer(Bukkit.getPlayer(uuid)));
        radioSongPlayer.setPlaying(true);
    }

    public void stopSongs() {
        if (this.radioSongPlayer != null) this.radioSongPlayer.setPlaying(false);
    }

    public Integer getDeathsOf(final UUID uuid) {
        if (deaths.containsKey(uuid) || deaths.get(uuid) != null)
            return deaths.get(uuid);
        return 0;
    }

    public Double getTotalDamageOf(final UUID uuid) {
        if (damageCounter.containsKey(uuid) || damageCounter.get(uuid) != null)
            return damageCounter.get(uuid);
        return 0D;
    }

    public LinkedList<Hologram> getHolograms() {
        return holograms;
    }

    public int getMaxDeaths() {
        return maxDeaths;
    }

    public LinkedList<Entity> getMobs() {
        return mobs;
    }

    public List<Entity> getAliveMobs() {
        return mobs.stream().filter(Entity::isAlive).collect(Collectors.toList());
    }

    public LinkedList<Puzzle> getCompletedPuzzles() {
        return completedPuzzles;
    }

    public void addCompletedPuzzle(final Puzzle puzzle) {
        if (!completedPuzzles.contains(puzzle)) completedPuzzles.add(puzzle);
    }

    public boolean isPuzzleCompleted(final Puzzle puzzle) {
        return completedPuzzles.contains(puzzle);
    }

    public Instant getTimeStarted() {
        return timeStarted;
    }

    public long getTimeElapsed() {
        return Duration.between(timeStarted, Instant.now()).toMillis();
    }

    public String getTime() {
        int minutes = (int) (getTimeElapsed() / (60 * 1000));
        int seconds = (int) ((getTimeElapsed() / 1000) % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public void setTimeStarted(Instant timeStarted) {
        this.timeStarted = timeStarted;
    }

    public LinkedList<DungeonRoom> getRoomsOpened() {
        return roomsOpened;
    }

    public void addRoomOpened(final DungeonRoom dungeonRoom) {
        if (this.rooms.contains(dungeonRoom) && !this.roomsOpened.contains(dungeonRoom))
            roomsOpened.add(dungeonRoom);
    }

    public Cuboid getCuboid() {
        return cuboid;
    }


    public DungeonGeneration getDungeonGeneration() {
        return dungeonGeneration;
    }

    public void setDungeonGeneration(DungeonGeneration dungeonGeneration) {
        this.dungeonGeneration = dungeonGeneration;
    }

    public DungeonDifficulty getDungeonDifficulty() {
        return dungeonDifficulty;
    }

    public void setDungeonDifficulty(DungeonDifficulty dungeonDifficulty) {
        this.dungeonDifficulty = dungeonDifficulty;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public void addRoom(final DungeonRoom room) {
        if (!rooms.contains(room)) rooms.add(room);
    }

    public void addPlayer(final UUID uuid) {
        final List<UUID> players = new ArrayList<>(this.players);
        if (!getPlayers().contains(uuid)) players.add(uuid);
        this.players = players;
    }

    public void removePlayer(final UUID uuid) {
        final List<UUID> players = new ArrayList<>(this.players);
        if (getPlayers().contains(uuid)) {
            rooms.forEach(room -> {
                if (room.getWatchers().contains(uuid)) {
                    room.getWatchers().remove(uuid);
                }
            });
            players.remove(uuid);
        }
        this.players = players;
    }

    public LinkedList<DungeonRoom> getRooms() {
        return rooms;
    }

    public int getID() {
        return ID;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public int getProgression() {
        //mobs = 60%
        //puzzles = 20%
        //chests = 20% TO ADD LATER
        int mobProgression = 0;
        if (mobs.size() > 0)
            mobProgression = ((mobs.size() - getAliveMobs().size()) * 60)/mobs.size();
        int puzzles = rooms.stream()
                .filter(r -> r.getTemplate().getName().contains("PUZZLE"))
                .collect(Collectors.toList()).size();
        int puzzlesProgression = puzzles > 0 ? (completedPuzzles.size() * 20 / puzzles) : 0;
        int anythingLeft = 0;

        return mobProgression + puzzlesProgression + anythingLeft;
    }
}
