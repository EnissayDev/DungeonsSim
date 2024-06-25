package org.enissay.dungeonssim.dungeon.party;

import org.bukkit.Bukkit;
import org.enissay.dungeonssim.dungeon.system.Dungeon;

import java.util.*;

public class DungeonParty {

    private int ID, maxPlayers;
    private String name;
    private Dungeon dungeon;
    private DungeonPartyStatus status;
    private boolean isPublic;
    private Map<UUID, DungeonRole> players = new HashMap<>();

    public DungeonParty(int ID, int maxPlayers, String name, Dungeon dungeon, boolean isPublic) {
        this.ID = ID;
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.status = DungeonPartyStatus.LOBBY;
        this.dungeon = dungeon;
        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        this.isPublic = aPublic;
    }

    public DungeonPartyStatus getStatus() {
        return status;
    }

    public void setStatus(DungeonPartyStatus status) {
        this.status = status;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Map<UUID, DungeonRole> getPlayers() {
        return players;
    }

    public void addPlayer(final UUID uuid, final DungeonRole dungeonRole) {
        final Map<UUID, DungeonRole> players = new HashMap<>(this.players);
        if (!getPlayers().keySet().contains(uuid) && this.players.size() < maxPlayers) players.put(uuid, dungeonRole);
        this.players = players;
        if (players.size() >= maxPlayers) setStatus(DungeonPartyStatus.FULL);
    }

    public void removePlayer(final UUID uuid) {
        final Map<UUID, DungeonRole> players = new HashMap<>(this.players);
        if (getPlayers().keySet().contains(uuid)) players.remove(uuid);
        this.players = players;
        if (getStatus() != DungeonPartyStatus.PLAYING && players.size() < maxPlayers) setStatus(DungeonPartyStatus.LOBBY);
    }

    public List<UUID> getPlayers(final DungeonRole dungeonRole) {
        List<UUID> players = new ArrayList<>();
        this.players.keySet().forEach(partyPlayer -> {
            if (this.players.get(partyPlayer) == dungeonRole) players.add(partyPlayer);
        });
        return players;
    }

    public DungeonRole getRoleOf(final UUID uuid) {
        return players.get(uuid);
    }


    public void sendMessageToMembers(final String message) {
        getPlayers().forEach(((uuid, dungeonRole) -> {
            if (Bukkit.getOfflinePlayer(uuid).isOnline())
                Bukkit.getPlayer(uuid).sendMessage(message.replace("&", "§"));
        }));
    }
}
