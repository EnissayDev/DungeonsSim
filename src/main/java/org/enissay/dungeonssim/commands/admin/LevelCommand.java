package org.enissay.dungeonssim.commands.admin;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;
import org.enissay.dungeonssim.utils.LevelUtil;

@Command(name = "level")
public class LevelCommand {

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
    public void set(@Context CommandSender sender, @Arg Player player, @Arg int newLevel) {
        final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfileByName(player.getName());
        if (sender instanceof Player && ProfilesHandler.findProfileByName(sender.getName()) == null) sender.sendMessage(ChatColor.RED + "Your profile does not exist in the database.");
        else if (sender instanceof Player && !Rank.hasPermissionsOf(((Player) sender).getPlayer(), Rank.ADMIN)) sender.sendMessage(ChatColor.RED + "You do not have enough permissions.");
        else if (dungeonPlayer == null) sender.sendMessage(ChatColor.RED + "This player does not exist in the database.");
        else {
            if (newLevel > 0) {
                ProfilesHandler.setEXP(player.getUniqueId().toString(), LevelUtil.minExpForLevel(newLevel));
                sendNormal(sender, "Updated " + player.getName() + "'s level to " + newLevel + " (" + LevelUtil.minExpForLevel(newLevel) + " EXP)");
            }
        }
    }
}
