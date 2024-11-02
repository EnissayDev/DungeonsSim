package org.enissay.dungeonssim.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public class GUIListener implements Listener {

    private final GUIManager guiManager;

    public GUIListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        this.guiManager.handleClick(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        this.guiManager.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.guiManager.handleClose(event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        this.guiManager.handleDrag(event);
    }

    @EventHandler
    public void test(InventoryMoveItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void test(InventoryInteractEvent event) {
        event.setCancelled(true);
    }

}