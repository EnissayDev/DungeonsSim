package org.enissay.dungeonssim.dungeon.party;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.handlers.PartyHandler;
import org.enissay.dungeonssim.utils.RepeatingTask;

import java.util.UUID;

public class DungeonInvite {
    private final UUID invitedPlayer;
    private final DungeonParty party;
    private boolean cancelled;
    private RepeatingTask expiryChecker;

    public DungeonInvite(UUID invitedPlayer, DungeonParty party) {
        this.invitedPlayer = invitedPlayer;
        this.party = party;
    }

    private void createTask() {
        this.expiryChecker = new RepeatingTask(0, 20) {
            int timePassed = 0; // In seconds

            @Override
            public void run() {
                if (timePassed == 60 && !cancelled) {
                    // Remove the invite
                    if (invitedPlayer != null && Bukkit.getPlayer(invitedPlayer).isOnline()) {
                        Bukkit.getPlayer(invitedPlayer).sendMessage(ChatColor.RED + "Invitation expired from party " + DungeonInvite.this.getPartyInvitation().getName());
                        final Player leader = Bukkit.getPlayer(party.getPlayers(DungeonRole.HOST).get(0));
                        if (leader != null && leader.isOnline()) {
                            leader.sendMessage(ChatColor.RED + "Invitation expired for " + Bukkit.getPlayer(invitedPlayer).getName());
                        }
                    }
                    PartyHandler.getInvitations().remove(DungeonInvite.this);
                    this.cancel();
                }
                timePassed ++;
            }
        };
    }

    public UUID getInvitedPlayer() {
        return invitedPlayer;
    }

    public DungeonParty getPartyInvitation() {
        return party;
    }

    public void accepted() {
        //PartyUtils.findParty(inviter).joinParty(invitedPlayer);
        PartyHandler.removeInvitation(invitedPlayer);
        PartyHandler.getInvitations().remove(DungeonInvite.this);
        //PartyHandler.getInvitations().remove(this);
        cancelled = true;
        this.expiryChecker.cancel();
        //Bukkit.getScheduler().cancelTask(expiryChecker.getTaskId());
    }

    public boolean isInvited() {
        boolean isInvited = false;

        for (DungeonInvite invite : PartyHandler.getInvitations()) {
            if (invite.getInvitedPlayer() == invitedPlayer && invite.getPartyInvitation() == party) {
                isInvited = true;
                break;
            }
        }

        return isInvited;
    }

    public RepeatingTask getExpiryChecker() {
        return expiryChecker;
    }

    public void create() {
        //PartyHandler.addInvitation(invitedPlayer, party);
        PartyHandler.getInvitations().add(DungeonInvite.this);
        createTask();
        this.expiryChecker.run();
    }
}
