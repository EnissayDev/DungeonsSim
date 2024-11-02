package org.enissay.dungeonssim.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;
import org.enissay.dungeonssim.utils.FormatUtil;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(event.getPlayer().getUniqueId().toString());
        if (dungeonPlayer != null && dungeonPlayer.getRank() != null)
            event.setFormat(ChatColor.GRAY + "[Lvl. " + FormatUtil.getLvlText(dungeonPlayer.getLevel()) + ChatColor.GRAY + "] " + dungeonPlayer.getRank().getColor().toString() + dungeonPlayer.getRank().name() + " " + "%1$s§f: %2$s");
        if (event.getMessage().startsWith("!") && dungeonPlayer != null && dungeonPlayer.getRank() != null && Rank.isStaff(event.getPlayer())){
            String prefix = "&b&lSTAFF &8- &r";
            event.setCancelled(true);

            Bukkit.getOnlinePlayers().forEach(players -> {
                if (dungeonPlayer != null && Rank.isStaff(players)) {
                    players.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + dungeonPlayer.getRank().getColor().toString() + dungeonPlayer.getRank().name() + " " + event.getPlayer().getName() + "&f:&b " + event.getMessage().replaceFirst("!", "")));
                }
            });
        }
    }
}
