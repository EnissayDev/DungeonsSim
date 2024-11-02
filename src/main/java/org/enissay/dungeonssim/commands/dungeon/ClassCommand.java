package org.enissay.dungeonssim.commands.dungeon;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.gui.InventoryGUI;
import org.enissay.dungeonssim.gui.impl.ClassGUI;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;

@Command(name = "class")
public class ClassCommand {
    private void sendInfo(final Player player, final String msg) {
        sendMessage(player, "&7" + msg);
    }

    private void sendStat(final Player player, final String key, final int value) {
        sendMessage(player, "&7" + key + ": &e" + value);
    }

    private void sendStat(final Player player, final String key, final String value) {
        sendMessage(player, "&7" + key + ": &e" + value);
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
    private void cmd(@Context CommandSender sender) {
        if (sender instanceof Player player) {
            if (DungeonHandler.isPlayerInADungeon(player)) {
                sendError(player, "You can't change class while in a Dungeon.");
                return;
            }
            InventoryGUI inventoryGUI = new ClassGUI();
            DungeonsSim.getInstance().getGuiManager().openGUI(inventoryGUI, player);
        }
    }
}
