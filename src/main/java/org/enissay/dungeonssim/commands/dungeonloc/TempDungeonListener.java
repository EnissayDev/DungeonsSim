package org.enissay.dungeonssim.commands.dungeonloc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.dungeon.templates.RoomLocation;
import org.enissay.dungeonssim.utils.ItemUtils;

public class TempDungeonListener implements Listener {

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final TempDungeonBuilds temp = TempDungeonBuildsManager.get(player.getUniqueId());
        final ItemStack item = ItemUtils.item(Material.STICK, ChatColor.YELLOW + "Dungeon Room Selector", "",ChatColor.GRAY + "Like worldedit selection");

        if (temp != null && temp.getName() != null && player.getItemInHand().isSimilar(item)) {
            event.setCancelled(true);
            switch (event.getAction()) {
                case LEFT_CLICK_BLOCK:
                    final TempDungeonBuilds newTemp1 = temp;
                    final RoomLocation roomLocation1 = new RoomLocation(event.getClickedBlock().getX(),event.getClickedBlock().getY(),event.getClickedBlock().getZ());
                    newTemp1.putLocation("location_1", roomLocation1);

                    player.sendMessage(ChatColor.GREEN + "Selected position 1 for " + ChatColor.YELLOW + temp.getName()
                            + " " + ChatColor.GREEN + betterLoc(temp.getLocation1()));

                    TempDungeonBuildsManager.put(player.getUniqueId(), newTemp1);
                    sendInformations(player, newTemp1);
                    break;
                case RIGHT_CLICK_BLOCK:
                    final TempDungeonBuilds newTemp2 = temp;
                    final RoomLocation roomLocation2 = new RoomLocation(event.getClickedBlock().getX(),event.getClickedBlock().getY(),event.getClickedBlock().getZ());
                    newTemp2.putLocation("location_2", roomLocation2);

                    player.sendMessage(ChatColor.GREEN + "Selected position 2 for " + ChatColor.YELLOW + temp.getName()
                            + ChatColor.GREEN + betterLoc(temp.getLocation2()));

                    TempDungeonBuildsManager.put(player.getUniqueId(), newTemp2);
                    sendInformations(player, newTemp2);
                    break;
            }
        }
    }

    public void sendInformations(final Player player, final TempDungeonBuilds temp) {
        if (temp != null) {
            //final Map<String, RoomLocation> locations = temp.getRoomLocations();

            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "-> Current Informations for Dungeon Room " + ChatColor.YELLOW + (temp.getName() != null ? temp.getName() : ChatColor.RED + "?"));
            player.sendMessage(ChatColor.GREEN + "Location 1: " + ChatColor.YELLOW + (temp.getLocation1() != null ?
                    betterLoc(temp.getLocation1()) : ChatColor.RED + betterLoc(temp.getLocation1())));
            player.sendMessage(ChatColor.GREEN + "Location 2: " + ChatColor.YELLOW + (temp.getLocation2() != null ?
                    betterLoc(temp.getLocation2()) : ChatColor.RED + betterLoc(temp.getLocation2())));
            player.sendMessage("");
            if (temp.getLocation1() != null && temp.getLocation2() != null) {
                player.sendMessage(ChatColor.GREEN + "Seems like you finished if you're certain execute /dungeonloc confirmbuild");
                player.sendMessage("");
            }
        }else player.sendMessage(ChatColor.RED + "Couldn't find your session");
    }

    public String betterLoc(final RoomLocation location) {
        return location != null ? "(" + location.getX() + ", " + location.getY() + ", " + location.getZ() + ")" : "undefined";
    }
}
