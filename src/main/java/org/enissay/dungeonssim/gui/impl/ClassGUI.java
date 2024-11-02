package org.enissay.dungeonssim.gui.impl;

import com.google.common.collect.Lists;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.gui.InventoryButton;
import org.enissay.dungeonssim.gui.InventoryGUI;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.PlayerClass;
import org.enissay.dungeonssim.utils.InventoryFill;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassGUI extends InventoryGUI {
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 5 * 9, "CLASS");
    }

    @Override
    public void decorate(Player player) {
        InventoryFill fill = new InventoryFill(this.getInventory(), 5 * 9);
        fill.fillSidesWithItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        final InventoryButton warriorButton = new InventoryButton();
        final InventoryButton mageButton = new InventoryButton();
        final InventoryButton tankButton = new InventoryButton();
        final InventoryButton archerButton = new InventoryButton();

        ItemStack warriorItem = new ItemBuilder(Material.IRON_SWORD)
                .name(PlayerClass.WARRIOR.getColor() + "Warrior Class")
                .lore("",
                        "&7This is a melee-based class,",
                        "&7so you will have to attack mobs",
                        "&7by approaching them.",
                        "")
                .build();

        ItemStack mageItem = new ItemBuilder(Material.ENCHANTED_BOOK)
                .name(PlayerClass.MAGE.getColor() + "Mage Class")
                .lore("",
                        "&7This is a ranged-based class,",
                        "&7so you will have to stay at a distance",
                        "&7while attacking mobs with spells.",
                        "")
                .build();

        ItemStack tankItem = new ItemBuilder(Material.SHIELD)
                .name(PlayerClass.TANK.getColor() + "Tank Class")
                .lore("",
                        "&7This is a defense-based class,",
                        "&7so you will absorb damage",
                        "&7and protect your allies.",
                        "")
                .build();
        ItemStack archerItem = new ItemBuilder(Material.BOW)
                .name(PlayerClass.ARCHER.getColor() + "Archer Class")
                .lore("",
                        "&7This is a ranged-based class,",
                        "&7so you will have to stay at a distance",
                        "&7while attacking mobs with spells.",
                        "")
                .build();

        Map<InventoryButton, PlayerClass> playerClassMap = new HashMap<>();

        playerClassMap.put(warriorButton, PlayerClass.WARRIOR);
        playerClassMap.put(mageButton, PlayerClass.MAGE);
        playerClassMap.put(tankButton, PlayerClass.TANK);
        playerClassMap.put(archerButton, PlayerClass.ARCHER);

        List<InventoryButton> buttons = Arrays.asList(warriorButton, mageButton, tankButton, archerButton);
        List<ItemStack> items = Arrays.asList(warriorItem, mageItem, tankItem, archerItem);

        Map<InventoryButton, ItemStack> buttonItemMap = new HashMap<>();
        for (int i = 0; i < buttons.size(); i++) {
            buttonItemMap.put(buttons.get(i), items.get(i));
        }

        // Example usage: Assign items to buttons
        buttonItemMap.forEach((button, item) -> {
            button.creator(player1 -> item).consumer(event -> {
                event.setCancelled(true);
                final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
                final PlayerClass playerClass = playerClassMap.get(button);
                if (playerClass != PlayerClass.WARRIOR) {
                    player.sendMessage(ChatColor.RED + "This class's items are still in development.");
                    return;
                }
                if (dungeonPlayer.getPlayerClass() == playerClass) {
                    player.sendMessage(ChatColor.RED + "You already have this class.");
                    return;
                }
                if (dungeonPlayer != null && playerClass != null) {
                    dungeonPlayer.setPlayerClass(playerClass);
                    ProfilesHandler.updateProfile(player.getUniqueId().toString(), dungeonPlayer);
                    player.sendMessage(ChatColor.GREEN + "Your class has been changed to " + playerClass.getName() + ".");
                }else player.sendMessage(ChatColor.RED + "There was an error regarding your profile.");
                player.closeInventory();
            });
        });

        /*warriorButton.creator(player1 -> warriorItem).consumer(event -> {
            event.setCancelled(true);
            player.closeInventory();
            final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
            if (dungeonPlayer != null) {
                dungeonPlayer.setPlayerClass(PlayerClass.WARRIOR);
                ProfilesHandler.updateProfile(player.getUniqueId().toString(), dungeonPlayer);
                player.sendMessage(ChatColor.GREEN + "Your class has been changed to Warrior");
            }else player.sendMessage(ChatColor.RED + "There was an error regarding your profile.");
        });*/

        this.addButton(19, warriorButton);
        this.addButton(21, mageButton);
        this.addButton(23, tankButton);
        this.addButton(25, mageButton);

        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
        if (dungeonPlayer != null) {
            if (dungeonPlayer.getPlayerClass() == PlayerClass.NONE && event.getInventory().equals(this.getInventory())) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        final InventoryGUI gui = new ClassGUI();
                        DungeonsSim.getInstance().getGuiManager().openGUI(gui, player);
                        player.sendMessage(ChatColor.RED + "You have to choose a class!");
                    }
                }.runTaskLater(DungeonsSim.getInstance(), 1L);

                super.onClose(event);
            }
        }
        //super.onClose(event);
    }
}
