package org.enissay.dungeonssim.handlers;

import org.enissay.dungeonssim.dungeon.party.DungeonInvite;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PartyHandler {

    private static LinkedList<DungeonParty> dungeonsPartys = new LinkedList<>();
    private static LinkedList<DungeonInvite> invitations = new LinkedList<>();

    public static void addInvitation(UUID player, DungeonParty party) {
        invitations.add(new DungeonInvite(player, party));
    }

    public static boolean hasInvitation(UUID player, DungeonParty party) {
        return new DungeonInvite(player, party).isInvited();
    }

    public static boolean isNameAvailable(final String name) {
        return !(dungeonsPartys.stream().filter(dungeonParty -> dungeonParty.getName().equals(name)).toList().size() > 0);
    }

    public static DungeonParty getParty(final String name) {
        return dungeonsPartys.stream().filter(dungeonParty -> dungeonParty.getName().equals(name)).findFirst().orElse(null);
    }

    public static DungeonInvite getDungeonInvite(final UUID player) {
        AtomicReference<DungeonInvite> dungeonParty = new AtomicReference<>();
        invitations.forEach(invitation -> {
            if (invitation.getInvitedPlayer() == player) dungeonParty.set(invitation);
        });
        return dungeonParty.get();
    }

    public static LinkedList<DungeonInvite> getInvitations() {
        return invitations;
    }

    public static void removeInvitation(UUID player) {
        invitations.remove(player);
    }

    /*public static DungeonParty getInvitedParty(UUID player) {
        return invitations.get(player);
    }*/

    public static DungeonParty getPartyOf(UUID player) {
        AtomicReference<DungeonParty> dungeonParty = new AtomicReference();
        dungeonsPartys.forEach(dp -> {
            if (dp.getPlayers().keySet().contains(player)) dungeonParty.set(dp);
        });
        return dungeonParty.get();
    }
    public static LinkedList<DungeonParty> getDungeonPartys() {
        return dungeonsPartys;
    }

    public static void addDungeonParty(final DungeonParty dungeonParty) {
        if (!dungeonsPartys.contains(dungeonParty)) dungeonsPartys.add(dungeonParty);
    }

    public static void removeDungeonParty(final DungeonParty dungeonParty) {
        if (dungeonsPartys.contains(dungeonParty)) dungeonsPartys.remove(dungeonParty);
    }
}
