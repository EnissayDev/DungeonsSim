package org.enissay.dungeonssim.commands.admin;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;

@Command(name = "rank")
public class RankCommand {
    private void sendInfo(final CommandSender player, final String msg) {
        sendMessage(player, "&7" + msg);
    }

    private void sendNormal(final CommandSender player, final String msg) {
        sendMessage(player, "&a" + msg);
    }

    private void sendError(final CommandSender player, final String msg) {
        sendMessage(player, "&c" + msg);
    }

    private void sendMessage(final CommandSender player, final String msg) {
        player.sendMessage(msg.replace('&', '§'));
    }

    @Execute(name = "set")
    void rank(@Context CommandSender sender, @Arg OfflinePlayer offlinePlayer, @Arg Rank rank) {
        final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfileByName(offlinePlayer.getName());
        if (sender instanceof Player && ProfilesHandler.findProfileByName(sender.getName()) == null) sender.sendMessage(ChatColor.RED + "Your profile does not exist in the database.");
        else if (sender instanceof Player && !Rank.hasPermissionsOf(((Player) sender).getPlayer(), Rank.ADMIN)) sender.sendMessage(ChatColor.RED + "You do not have enough permissions.");
        else if (dungeonPlayer == null) sender.sendMessage(ChatColor.RED + "This player does not exist in the database.");
        else {
            dungeonPlayer.setRank(rank);
            ProfilesHandler.updateProfileByName(offlinePlayer.getName(), dungeonPlayer);
            sendNormal(sender, ChatColor.GREEN + "Successfully updated " + offlinePlayer.getName() + "'s rank.");
        }
    }
}
