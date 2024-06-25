package org.enissay.dungeonssim.commands.admin;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.BuilderMode;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;

@Command(name = "builder")
public class BuilderCommand {
    private void sendInfo(final Player player, final String msg) {
        sendMessage(player, "&7" + msg);
    }

    private void sendNormal(final Player player, final String msg) {
        sendMessage(player, "&a" + msg);
    }

    private void sendError(final Player player, final String msg) {
        sendMessage(player, "&c" + msg);
    }

    private void sendMessage(final Player player, final String msg) {
        player.sendMessage(msg.replace('&', '§'));
    }

    @Execute()
    void rank(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfileByName(player.getName());
            if (dungeonPlayer == null) sendError(player, "Your profile does not exist in the database.");
            else if (!Rank.hasPermissionsOf(((Player) sender).getPlayer(), Rank.ADMIN))
                sendError(player, "You do not have enough permissions.");
            else {
                final boolean builderMode = BuilderMode.canBuild((Player) sender);

                if (!builderMode) {
                    BuilderMode.putStatus((Player) sender, true);
                    sendNormal(player, "You can build/place.");
                } else {
                    BuilderMode.putStatus((Player) sender, false);
                    sendNormal(player, "You can't build/place anymore.");
                }
            }
        }
    }
}