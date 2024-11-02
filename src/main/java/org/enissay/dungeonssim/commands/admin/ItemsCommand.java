package org.enissay.dungeonssim.commands.admin;

import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;
import org.enissay.dungeonssim.utils.InventoryFill;
import org.enissay.dungeonssim.utils.Mode;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Command(name = "items")
public class ItemsCommand {

    @Execute()
    void cmd(@Context CommandSender commandSender) {
        if (commandSender instanceof Player) {
            final Player player = ((Player) commandSender).getPlayer();
            if (!Rank.hasPermissionsOf(player, Rank.ADMIN)) {
                player.sendMessage(ChatColor.RED + "You do not have enough permissions to execute this command.");
                return;
            }
            final SpiGUI spiGUI = DungeonsSim.getInstance().getSpiGUI();
            final SGMenu GUI = spiGUI.create("ITEMS", 5);
            GUI.setToolbarBuilder((slot, page, defaultType, menu) -> {
                switch (defaultType) {
                    case PREV_BUTTON:
                        if (menu.getCurrentPage() > 0) return new SGButton(new ItemBuilder(Material.ARROW)
                                .name("&7\u2190 Previous Page")
                                .lore("",
                                        "&fClick to move to",
                                        "&fpage &a" + (menu.getCurrentPage()) + "&f.",
                                        ""
                                ).build()
                        ).withListener(event -> {
                            event.setCancelled(true);
                            menu.previousPage(event.getWhoClicked());
                        });
                        else return null;
                    case CURRENT_BUTTON:
                        return new SGButton(
                                new ItemBuilder(Material.BOOK)
                                        .name(ChatColor.YELLOW + "Page: " + ChatColor.GRAY + "(" + ChatColor.AQUA + (menu.getCurrentPage() + 1) + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_AQUA + menu.getMaxPage() + ChatColor.GRAY + ")")
                                        .build()
                        ).withListener((event) -> {
                            menu.refreshInventory(event.getWhoClicked());
                        });
                    case NEXT_BUTTON:
                        if (menu.getCurrentPage() < menu.getMaxPage() - 1)
                            return new SGButton(new ItemBuilder(Material.ARROW)
                                    .name("&7Next page \u2192")
                                    .lore("",
                                            "&fClick to move to",
                                            "&fpage &a" + (menu.getCurrentPage() + 2) + "&f.",
                                            ""
                                    ).build()
                            ).withListener(event -> {
                                event.setCancelled(true);
                                menu.nextPage(event.getWhoClicked());
                            });
                        else return null;
                }

                // Fallback to rendering the default button for a slot.
                return spiGUI.getDefaultToolbarBuilder().buildToolbarButton(slot, page, defaultType, menu);
            });

            final LinkedList<ItemsList> items = new LinkedList<>();
            Arrays.stream(ItemsList.values()).forEach(i -> items.add(i));

            for (int in = 0; in < items.size(); in++) {
                final Map<String, String> attributes = new HashMap<>();
                final ItemsList chosen = items.get(in);

                if (!chosen.isNature()) {
                    attributes.put("owner", player.getName());
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    attributes.put("obtain_date", dateFormat.format(Date.from(Instant.now())));
                    attributes.put("edition_id", Mode.getString(10, Mode.ALPHANUMERIC).toUpperCase());

                    chosen.getAttributes().forEach((key, value) -> {
                        //if (!key.equalsIgnoreCase("attack_damage"))
                        attributes.put(key, value);
                    });

                    String damage = chosen.getAttributes().get("attack_damage");
                    if (damage != null) {
                        String[] split = damage.split(",");
                        if (split != null) {
                            int minDamage = Integer.valueOf(split[0]);
                            int maxDamage = Integer.valueOf(split[1]);
                            attributes.put("attack_damage", "between " + minDamage + " and " + maxDamage + " damage.");
                        }
                    }
                }

                final ItemStack result = chosen.toItemNoOption(attributes);
                final ItemMeta meta = result.getItemMeta();
                final List<String> lore = meta.getLore();

                lore.add("");

                meta.setLore(lore);
                result.setItemMeta(meta);
                final SGButton sgButton = new SGButton(result).withListener(event -> {
                    event.setCancelled(true);
                    //player.closeInventory();

                    final DungeonPlayer profile = ProfilesHandler.findProfile(player.getUniqueId().toString());

                    if (profile != null) {
                        if (!Objects.isNull(profile.getRank()) && Rank.hasPermissionsOf(player, Rank.ADMIN)) {

                            player.sendMessage(ChatColor.GREEN + "Successfully obtained the item " + chosen.getRarity().getColor() + chosen.getName() + ChatColor.GREEN);
                            player.getInventory().addItem(ItemsList.createItem(player, chosen));
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have enough permissions to obtain this item.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "An error occurred with your profile.");
                    }
                });
                GUI.setOnPageChange(inventory -> {
                    player.updateInventory();
                    GUI.refreshInventory(player);

                });
                GUI.addButton(sgButton);
            }

            GUI.refreshInventory(player);
            GUI.getOnPageChange().accept(GUI);
            player.updateInventory();
            player.openInventory(GUI.getInventory());
        }
        return;
    }
}
