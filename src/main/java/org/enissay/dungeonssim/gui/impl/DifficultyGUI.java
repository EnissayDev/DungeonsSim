package org.enissay.dungeonssim.gui.impl;

import com.samjakob.spigui.item.ItemBuilder;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.gui.InventoryButton;
import org.enissay.dungeonssim.gui.InventoryGUI;
import org.enissay.dungeonssim.profiles.PlayerClass;
import org.enissay.dungeonssim.utils.InventoryFill;

public class DifficultyGUI extends InventoryGUI {
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 5 * 9, "DIFFICULTY");
    }

    @Override
    public void decorate(Player player) {
        InventoryFill fill = new InventoryFill(this.getInventory(), 5 * 9);
        fill.fillSidesWithItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        final InventoryButton easyButton = new InventoryButton();
        final InventoryButton normalButton = new InventoryButton();
        final InventoryButton hardButton = new InventoryButton();
        final InventoryButton nightMareButton = new InventoryButton();
        ItemStack easyItem = new ItemBuilder(NBTEditor.getHead("http://textures.minecraft.net/texture/5c7772c7cdcddb6b79d5525f9dcebc748aabdae38d9e38eea7fe78a501de6ede"))
                .name("&a&lEASY")
                .lore("",
                        "&7Click to enter a dungeon with this difficulty.",
                        "")
                .build();
        ItemStack comingItem = new ItemBuilder(NBTEditor.getHead("http://textures.minecraft.net/texture/d5d20330da59c207d78352838e91a48ea1e42b45a9893226144b251fe9b9d535"))
                .name("&c&l??")
                .lore("",
                        "&cW.I.P.",
                        "")
                .build();

        easyButton.creator(player1 -> easyItem).consumer(event -> {
            player.closeInventory();
            player.performCommand("dp start EASY");
            event.setCancelled(true);
        });

        normalButton.creator(player1 -> comingItem).consumer(event -> {

        });

        hardButton.creator(player1 -> comingItem).consumer(event -> {

        });

        nightMareButton.creator(player1 -> comingItem).consumer(event -> {

        });

        this.addButton(20, easyButton);
        this.addButton(22, normalButton);
        this.addButton(24, hardButton);
        this.addButton(31, nightMareButton);
        super.decorate(player);
    }
}


