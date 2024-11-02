package org.enissay.dungeonssim.listeners;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.PlayerClass;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemListener implements Listener {

    private final Map<Map<Player, ItemStack>, Instant> map = new HashMap<>();

    // Set cooldown
    public void setCooldown(Map<Player, ItemStack> key, Duration duration) {
        map.put(key, Instant.now().plus(duration));
    }

    // Check if cooldown has expired
    public boolean hasCooldown(Player key, ItemStack itemStack) {
        Map<Player, ItemStack> lol = new HashMap<>();
        lol.put(key, itemStack);
        Instant cooldown = map.get(lol);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    // Remove cooldown
    public Instant removeCooldown(Player key, ItemStack itemStack) {
        Map<Player, ItemStack> lol = new HashMap<>();
        lol.put(key, itemStack);
        return map.remove(key);
    }

    public Duration getRemainingCooldown(Player key, ItemStack itemStack) {
        Map<Player, ItemStack> lol = new HashMap<>();
        lol.put(key, itemStack);
        Instant cooldown = map.get(lol);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            return Duration.ZERO;
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (event.getItem() != null && !Objects.isNull(NBTEditor.getInt(event.getItem(), "ID")) && NBTEditor.getString(event.getItem(), "edition_id") != null) {
            final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(event.getPlayer().getUniqueId().toString());
            final ItemsList item = ItemsList.getItemByID(NBTEditor.getInt(event.getItem(), "ID"));
            if (item != null && dungeonPlayer != null && dungeonPlayer.getPlayerClass() == item.getItemClass() && item.canPlayerUseItem(event.getPlayer())) {
                if (!hasCooldown(event.getPlayer(), event.getItem())) {
                    final double cooldown = item.doAbility(event.getPlayer(), event.getAction(), NBTEditor.getString(event.getItem(), "attack_damage") != null ? Double.parseDouble(NBTEditor.getString(event.getItem(), "attack_damage")) : 0, event);
                    if (!Objects.isNull(cooldown) || cooldown >= 0) {
                        final Map<Player, ItemStack> map = new HashMap<>();
                        map.put(event.getPlayer(), event.getItem());
                        setCooldown(map, Duration.ofMillis((long) (cooldown * 1000)));
                        event.setCancelled(true);
                    }
                } else
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cItem is on cooldown: " + getRemainingCooldown(event.getPlayer(), event.getItem()).toSeconds() + " seconds"));
            }
            else if (item != null && item.getItemClass() != PlayerClass.NONE && item.canPlayerUseItem(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You do not match the item's required class!");
                event.setCancelled(true);
            }
            if (item != null && !item.canPlayerUseItem(event.getPlayer())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough level to use this item! (Lvl. " + item.getLevelReq() + " required)");
            }

        }

        //event.setCancelled((isInSafeZone(event.getPlayer()) && event.getAction() != Action.RIGHT_CLICK_AIR && event.getClickedBlock() != null && (event.getClickedBlock().getType() != Material.ENDER_CHEST)) && !BuilderMode.canBuild(event.getPlayer()));
    }

    @EventHandler
    public void onConsumption(PlayerItemConsumeEvent event) {
        final ItemStack item = event.getItem();
        if (item.getType() == Material.POTION && !Objects.isNull(NBTEditor.getInt(item, "ID"))) {
            final ItemsList customItem = ItemsList.getItemByID(NBTEditor.getInt(item, "ID"));
            if (customItem.name().contains("POTION_HEAL")) {
                int healthBonus = 25;
                if (NBTEditor.getString(item, "heal") != null)
                    healthBonus = Integer.valueOf(NBTEditor.getString(item, "heal"));
                final double maxHealth = event.getPlayer().getMaxHealth();
                final double health = event.getPlayer().getHealth();
                final double remaining = maxHealth - health;

                if (remaining >= healthBonus) event.getPlayer().setHealth(health + healthBonus);
                else event.getPlayer().setHealth(maxHealth);
                event.getPlayer().getInventory().remove(item);
                event.setCancelled(true);
            }
        }
    }

}
