package org.enissay.dungeonssim.dungeon.party;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;

import java.util.UUID;

public class PartyMember {

    private UUID uuid;
    private DungeonParty party;

    public PartyMember(UUID uuid, DungeonParty party) {
        this.uuid = uuid;
        this.party = party;
    }

    public String getName() {
        return getPlayer().getName();
    }

    public DungeonParty getParty() {
        return party;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }
}
