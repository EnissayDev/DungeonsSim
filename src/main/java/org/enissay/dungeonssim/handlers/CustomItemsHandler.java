package org.enissay.dungeonssim.handlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.profiles.PlayerClass;
import org.enissay.dungeonssim.items.ItemType;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.items.Rarity;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;
import org.enissay.dungeonssim.utils.FormatUtil;
import org.enissay.dungeonssim.utils.LevelUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomItemsHandler {

    public static void init() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    PlayerInventory inventory = player.getInventory();
                    checkForDuplicateItems(player, inventory);
                    updateInventoryItems(inventory);
                    updateActionBar(player);
                    removeDamageParticles();
                    taskHealth();
                });
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 1, 2 * 20);
    }

    public static void removeDamageParticles() {
        DungeonsSim.getInstance().getManager().addPacketListener(new PacketAdapter(DungeonsSim.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_PARTICLES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                if (event.getPacketType() != PacketType.Play.Server.WORLD_PARTICLES)
                    return;

                if (packet.getNewParticles().read(0).getParticle() == Particle.DAMAGE_INDICATOR)
                    event.setCancelled(true);
            }
        });
    }

    private static void checkForDuplicateItems(Player player, PlayerInventory inventory) {
        Map<String, Long> frequencyMap = Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .map(item -> NBTEditor.getString(item, "edition_id") != null ? NBTEditor.getString(item, "edition_id") : "DEFAULT")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Set<String> duplicates = frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (duplicates.size() > 1 && ProfilesHandler.findProfile(player.getUniqueId().toString()) != null && Rank.isStaff(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lDUPE DETECTED! \n&8- &fPlayer: &6" + player.getName() + "\n&8- &fDuplications: &4" + (duplicates.size() - 1)));
        }
    }

    private static void updateInventoryItems(PlayerInventory inventory) {
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                try {
                    synchronized (inventory) {
                        ItemStack modifiedItem = CustomItemsHandler.getItem(item);
                        if (modifiedItem != null) {
                            inventory.setItem(i, modifiedItem);
                        }
                    }
                } catch (ConcurrentModificationException e) {
                    System.err.println("ConcurrentModificationException: " + e.getMessage());
                }
            }
        }
    }

    private static void taskHealth(){
        new BukkitRunnable(){

            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    double finalHealth = 100;
                    for (ItemStack a : armor) {
                        if (a != null) {
                            NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(a);
                            if (NBTEditor.getString(compound, "tag", "health") != null) {
                                int health = Integer.valueOf(NBTEditor.getString(compound, "tag", "health"));
                                finalHealth += health;
                            }
                        }
                    }
                    player.setMaxHealth(finalHealth);
                    //player.setHealth(player.getMaxHealth());
                });
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, 20L);
    }

    private static void updateActionBar(Player player) {
        ItemStack itemInHand = player.getItemInHand();
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.US));

        stringBuilder.append(ChatColor.RED)
                .append(df.format(player.getHealth()))
                .append("/")
                .append(df.format(player.getMaxHealth()))
                .append("❤");

        if (itemInHand != null && NBTEditor.getString(itemInHand, "attack_damage") != null) {
            int attackDamage = Integer.parseInt(NBTEditor.getString(itemInHand, "attack_damage"));
            int upgrades = Integer.parseInt(NBTEditor.getString(itemInHand, "upgrades"));

            stringBuilder.append(ChatColor.DARK_GRAY)
                    .append("   ")
                    .append(ChatColor.RED)
                    .append(attackDamage + upgrades)
                    .append("\u2694");
        }

        DungeonPlayer profile = ProfilesHandler.findProfile(player.getUniqueId().toString());
        if (profile != null) {
            final long currentEXP = profile.getExp();
            final long maxEXP = profile.getMaxEXP();
            stringBuilder.append(ChatColor.DARK_GRAY)
                    .append("   ")
                    .append(ChatColor.YELLOW)
                    .append((int)profile.getCoins())
                    .append("\u29bf")
                    .append("   ")
                    .append(ChatColor.DARK_AQUA)
                    .append("Lvl. " + profile.getLevel() + " (" + FormatUtil.format(currentEXP) + "/" + FormatUtil.format(maxEXP) + " EXP)");
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(stringBuilder.toString()));
    }

    public static ItemStack getItem(ItemStack ogItem) {
        if (ogItem == null || ogItem.getItemMeta() == null || ogItem.getItemMeta().getDisplayName() == null) {
            return ogItem;
        }

        ItemMeta meta = ogItem.getItemMeta();
        List<String> modifiedLore = new ArrayList<>();
        AtomicReference<Rarity> rarity = new AtomicReference<>(Rarity.COMMON);

        for (ItemsList item : getAllRegisteredItems()) {
            if (matchesItem(ogItem, item)) {
                updateMetaAndLore(ogItem, meta, modifiedLore, item);
                rarity.set(item.getRarity());
                //Bukkit.broadcastMessage("recognized " + item.getName());
                break;
            } else if (ogItem.getType() == item.getMat() && (item.isNature() && NBTEditor.getInt(ogItem, "ID") == 0 || Objects.isNull(NBTEditor.getInt(ogItem, "ID")))) {
                updateMetaAndLore(ogItem, meta, modifiedLore, item);
                rarity.set(item.getRarity());
                break;
            }
        }
        finalizeItemMeta(meta, modifiedLore, rarity.get());
        ogItem.setItemMeta(meta);
        return ogItem;
    }

    private static boolean matchesItem(ItemStack ogItem, ItemsList item) {
        int itemId = NBTEditor.getInt(ogItem, "ID");
        return ogItem.getType() == item.getMat() &&
                (item.isNature() && itemId == 0 || itemId == item.getId()) &&
                NBTEditor.getString(ogItem, "edition_id") != null;
    }

    private static void updateMetaAndLore(ItemStack ogItem, ItemMeta meta, List<String> modifiedLore, ItemsList item) {
        meta.setDisplayName(item.getRarity().getColor() + item.getName());
        if (!item.isNature() && ogItem != null && ogItem.getType() != null && ogItem.getType() != Material.POTION)
            modifiedLore.add(replacePlaceholders(ogItem, "&8" + item.getItemType().name()));
        Arrays.asList(item.getLore()).forEach(s -> modifiedLore.add(replacePlaceholders(ogItem, s)));
        if (!item.isNature() && ogItem != null && ogItem.getType() != null && ogItem.getType() != Material.POTION) {
            modifiedLore.add("");
        }

        final PlayerClass itemClass = item.getItemClass();
        final ItemType itemType = item.getItemType();

        NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(ogItem);

        if (NBTEditor.getString(compound, "tag","health") != null) modifiedLore.add("&8○ &fHealth: &a+$h❤");
        if (NBTEditor.getString(compound, "tag","upgrades") != null) modifiedLore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
        if (itemClass != PlayerClass.MAGE && NBTEditor.getString(compound, "tag","attack_damage") != null) modifiedLore.add("&8○ &fAttack damage: &c$ad &c\u2694");
        else if (NBTEditor.getString(compound, "tag","attack_damage") != null) modifiedLore.add("&8○ &fMagic damage: &d$ad &d\u2694");
        if (NBTEditor.getString(compound, "tag","critical_chance") != null) modifiedLore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");

        /*switch (itemClass) {
            case TANK:
            case WARRIOR:
                switch (itemType) {
                    case ARMOR:
                        modifiedLore.add("&8○ &fHealth: &a+$h❤");
                        modifiedLore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                        break;
                    case WEAPON:
                        modifiedLore.add("&8○ &fAttack damage: &c$ad &c\u2694");
                        modifiedLore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");
                        modifiedLore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                        break;
                }
                break;
            case MAGE:
                switch (itemType) {
                    case ARMOR:
                        modifiedLore.add("&8○ &fHealth: &a+$h❤");
                        modifiedLore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                        break;
                    case WEAPON:
                        modifiedLore.add("&8○ &fMagic damage: &d$ad &5\u2694");
                        modifiedLore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");
                        modifiedLore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                        break;
                }
                break;
        }*/
        if (!item.isNature() && ogItem != null && ogItem.getType() != null && ogItem.getType() != Material.POTION) {
            modifiedLore.add("");
            modifiedLore.add("&8○ &fItem Class: " + item.getItemClass().getText());
            modifiedLore.add("&8○ &fLevel Required: " + FormatUtil.getLvlText(item.getLevelReq()));
            modifiedLore.add("");
            modifiedLore.add("&8\u2192 &e&lOWNER&7:&r $p");
            modifiedLore.add("");
            modifiedLore.add("&8#$e &8- $d");
        }
        modifiedLore.forEach(s -> modifiedLore.set(modifiedLore.indexOf(s), replacePlaceholders(ogItem, s)));

        /*for (String itemLore : item.getLore()) {
            String modifiedLoreElement = replacePlaceholders(ogItem, itemLore);
            modifiedLore.add(modifiedLoreElement);
        }*/
    }

    private static String replacePlaceholders(ItemStack ogItem, String lore) {
        return lore.replace("$d", getNBTString(ogItem, "obtain_date"))
                .replace("$p", getOwnerString(ogItem))
                .replace("$e", getNBTString(ogItem, "edition_id"))
                .replace("$ad", getNBTString(ogItem, "attack_damage"))
                .replace("$cc", getNBTString(ogItem, "critical_chance"))
                .replace("$u", getNBTString(ogItem, "upgrades"))
                .replace("$h", getNBTString(ogItem, "health"))
                .replace('&', '§');
    }

    private static String getNBTString(ItemStack item, String key) {
        return NBTEditor.getString(item, key) == null ? "" : NBTEditor.getString(item, key);
    }

    private static String getOwnerString(ItemStack item) {
        String owner = NBTEditor.getString(item, "owner");
        if (owner == null) return "";

        DungeonPlayer profile = ProfilesHandler.findProfileByName(owner);
        String rankPrefix = (profile != null) ? profile.getRank().fullPrefix() : Rank.PLAYER.fullPrefix();
        return rankPrefix + " " + owner;
    }

    private static void finalizeItemMeta(ItemMeta meta, List<String> lore, Rarity rarity) {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
        meta.setUnbreakable(true);
        lore.add(rarity.toString());
        meta.setLore(lore);
    }

    public static ItemsList[] getAllRegisteredItems() {
        return ItemsList.values();
    }
}
