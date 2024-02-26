package org.enissay.dungeonssim.commands.dungeon;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import joptsimple.internal.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.dungeon.Dungeon;
import org.enissay.dungeonssim.handlers.DungeonHandler;

import java.util.ArrayList;
import java.util.List;

@Command(name = "dungeon")
public class DungeonCommand {

    @Execute(name = "create")
    void create(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            //if (dungeon == null)
        }
    }

    @Execute(name = "invite")
    void invite(@Context CommandSender sender, @Arg Player target) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            if (dungeon == null) {
                player.sendMessage(ChatColor.RED + "You don't have a dungeon party");
                return;
            }
            if (!target.getUniqueId().equals(player.getUniqueId()) && !dungeon.getPlayers().contains(target)) {
                dungeon.getPlayers().add(target.getUniqueId());
                target.sendMessage(ChatColor.GREEN + "You forcefully got invited cuh");
            }else player.sendMessage(ChatColor.RED + "Something isn't right");
        }
    }

    @Execute(name = "list")
    void list(@Context CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (DungeonHandler.getDungeons().size() > 0) {
                player.sendMessage(ChatColor.GREEN + "DUNGEONS: " + DungeonHandler.getDungeons().size());

                DungeonHandler.getDungeons().forEach(dungeon -> {
                    List<String> names = new ArrayList<>();
                    dungeon.getPlayers().forEach(uuid -> {
                        names.add(Bukkit.getPlayer(uuid).getName());
                    });
                    player.sendMessage(ChatColor.GREEN + "-> DUNGEON" + ChatColor.AQUA + "#" + dungeon.getID() + ChatColor.GREEN +
                            " - Rooms: " + ChatColor.YELLOW + dungeon.getRooms().size() + ChatColor.GREEN +
                            " - Players: " + ChatColor.YELLOW + String.join(",", names));
                });
            }else player.sendMessage(ChatColor.RED + "No dungeons available");
        }
    }

    @Execute(name = "generate")
    void generate(@Context CommandSender sender, @Arg Player target) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
        }
    }
}
