package org.enissay.dungeonssim.commands.dungeonloc;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.utils.ItemUtils;

@Command(name = "dungeonloc")
public class DungeonLocationsCommand {
    @Execute
    void generate(@Context CommandSender sender, @Arg String opt, @Arg String templateName, @Arg String roomName, @Arg double chance/*, @Arg int port*/) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            switch (opt) {
                case "addbuild":
                    if (roomName != null && templateName != null && DungeonHandler.getTemplate(templateName) != null) {
                        TempDungeonBuildsManager.put(player.getUniqueId(), new TempDungeonBuilds(templateName, roomName, chance));

                        final ItemStack item = ItemUtils.item(Material.STICK, ChatColor.YELLOW + "Dungeon Room Selector", "",ChatColor.GRAY + "Like worldedit selection");

                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GREEN + "Started selection session for dungeon: " + templateName + "/" + roomName);
                    }
                    break;
                /*case "confirmbuild":
                    final TempDungeonBuilds temp = TempDungeonBuildsManager.get(player.getUniqueId());
                    if (name != null && temp != null && temp.getName().equals(name) && temp.getLocation1() != null && temp.getLocation2() != null) {
                        //Save in json file: ../template/name_of_map.json
                        DungeonHandler.publishRoom(temp);
                    }else {
                        player.sendMessage(ChatColor.RED + "Something is missing...");
                    }
                    //Clear the session
                    TempDungeonBuildsManager.clear(player.getUniqueId());

                    player.sendMessage(ChatColor.GREEN + "Finished.");
                    break;*/
            }
        }
    }

    @Execute
    void confirm(@Context CommandSender sender, @Arg String opt/*, @Arg int port*/) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            switch (opt) {
                case "confirmbuild":
                    final TempDungeonBuilds temp = TempDungeonBuildsManager.get(player.getUniqueId());
                    if (temp != null&& temp.getLocation1() != null && temp.getLocation2() != null) {
                        //Save in json file: ../template/name_of_map.json
                        DungeonHandler.publishRoom(temp);
                    }else {
                        player.sendMessage(ChatColor.RED + "Something is missing...");
                    }
                    //Clear the session
                    TempDungeonBuildsManager.clear(player.getUniqueId());

                    player.sendMessage(ChatColor.GREEN + "Finished.");
                    break;
            }
        }
    }
}
