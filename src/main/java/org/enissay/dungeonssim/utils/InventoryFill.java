package org.enissay.dungeonssim.utils;

import com.google.common.collect.Lists;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryFill {

    private final Inventory inv;
    private final int size;
    private List<Integer> sideSlots = new ArrayList<>();

    public InventoryFill(Inventory inv, int size) {
        this.inv = inv;
        this.size = size;
    }

    public List<Integer> fillSidesWithItem(ItemStack item) {
        int size = this.size;
        int rows = size / 9;

        if(rows >= 3) {
            for (int i = 0; i <= 8; i++) {
                this.inv.setItem(i, item);

                sideSlots.add(i);
            }

            for(int s = 8; s < (size - 9); s += 9) {
                int lastSlot = s + 1;
                this.inv.setItem(s, item);
                this.inv.setItem(lastSlot, item);

                sideSlots.add(s);
                sideSlots.add(lastSlot);
            }

            for (int lr = (size - 9); lr < size; lr++) {
                this.inv.setItem(lr, item);

                sideSlots.add(lr);
            }
        }
        return sideSlots;
    }

    public List<Integer> getNonSideSlots() {
        List<Integer> availableSlots = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if(!this.sideSlots.contains(i)) {
                availableSlots.add(i);
            }
        }

        return availableSlots;
    }

    public List<Integer> getSideSlots() {
        List<Integer> availableSlots = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if(this.sideSlots.contains(i)) {
                availableSlots.add(i);
            }
        }

        return availableSlots;
    }
}
