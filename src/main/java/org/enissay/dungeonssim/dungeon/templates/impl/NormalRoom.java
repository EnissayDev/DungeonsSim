package org.enissay.dungeonssim.dungeon.templates.impl;

import eu.decentsoftware.holograms.api.DHAPI;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Lidded;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.events.DungeonFinishEvent;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.dungeon.DungeonTemplate;
import org.enissay.dungeonssim.dungeon.EventManager;
import org.enissay.dungeonssim.dungeon.templates.MonsterFrequency;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.entities.hostile.BossTest;
import org.enissay.dungeonssim.entities.hostile.CustomHostileMob;
import org.enissay.dungeonssim.entities.hostile.skeleton.UndeadSkeleton;
import org.enissay.dungeonssim.entities.hostile.spiders.Tarantula;
import org.enissay.dungeonssim.entities.hostile.undeads.Undead;
import org.enissay.dungeonssim.entities.hostile.undeads.UndeadFrozen;
import org.enissay.dungeonssim.entities.hostile.undeads.UndeadKnight;
import org.enissay.dungeonssim.entities.hostile.undeads.UndeadWarrior;
import org.enissay.dungeonssim.entities.passive.CustomNonHostileMob;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PartyHandler;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.utils.LuckUtil;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.util.*;

public class NormalRoom implements DungeonTemplate {

    Map<UUID, List<Block>> openedChests;

    @Override
    public String getName() {
        return "NORMAL_ROOM";
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public boolean hasUniqueRooms() {
        return true;
    }

    @Override
    public int getMinRooms() {
        return 1;
    }

    @Override
    public int getMaxRooms() {
        return 6;
    }

    @Override
    public void initEvents() {
        openedChests = new HashMap<>();
        /*EventManager.on(PlayerDeathEvent.class, event -> {

            event.setKeepInventory(true);
            event.setDeathMessage(null);
        });*/
        EventManager.on(PlayerRespawnEvent.class, event -> {
            final Player player = event.getPlayer();
            final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            if (dungeon != null) {
                final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(player.getLocation());
                if (dungeonRoom != null) {
                    final Location loc = dungeonRoom.getLocationFromTemplate("respawnLocation");
                    final Location spawnLoc = dungeon.getRooms().stream().filter(r -> r.getTemplate().getName().contains("SPAWN")).findFirst().orElse(null).getLocationFromTemplate("spawnLocation");
                    if (loc != null)
                        event.setRespawnLocation(loc);
                    else event.setRespawnLocation(spawnLoc);

                    int maxDeaths = dungeon.getMaxDeaths();
                    dungeon.addDeath(player.getUniqueId());
                    int deaths = dungeon.getDeathsOf(player.getUniqueId());
                    MessageUtils.broadcastDungeon(dungeon, "&c" + player.getName() + "&7 has died. &8[&c" + deaths + "&7/&c" + maxDeaths + "&8]", MessageUtils.BroadcastType.MESSAGE);
                    if (deaths >= maxDeaths) {
                        Bukkit.getPluginManager().callEvent(new DungeonFinishEvent(dungeon, PartyHandler.getPartyOf(player.getUniqueId()), false));
                        event.setRespawnLocation(DungeonsSim.getInstance().getSpawnLocation());
                    }
                }
            }
        });
        //Chest
        EventManager.on(PlayerInteractEvent.class, event -> {
            final Player player = event.getPlayer();
            final Map<ItemStack, Double> drops = new HashMap<>();

            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = ((PotionMeta) item.getItemMeta());
            meta.setColor(Color.AQUA);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 300 * 20, 1), true);
            item.setItemMeta(meta);

            ItemStack enderPearls  = ItemsList.createItem(player, ItemsList.ENDER_PEARL);
            enderPearls.setAmount(14);

            /*drops.put(ItemsList.createItem(player, ItemsList.WARRIOR_HELMET), 10D);
            drops.put(ItemsList.createItem(player, ItemsList.WARRIOR_CHESTPLATE), 10D);
            drops.put(ItemsList.createItem(player, ItemsList.WARRIOR_LEGGINGS), 10D);
            drops.put(ItemsList.createItem(player, ItemsList.WARRIOR_BOOTS), 10D);*/
            drops.put(ItemsList.createItem(player, ItemsList.POTION_SPEED_TIER1), 15D);
            drops.put(enderPearls, 24D);
            drops.put(ItemsList.createItem(player, ItemsList.POTION_HEAL_TIER1), 15D);
            drops.put(ItemsList.createItem(player, ItemsList.POTION_HEAL_TIER2), 15D);
            drops.put(ItemsList.createItem(player, ItemsList.POTION_HEAL_TIER3), 10D);
            drops.put(ItemsList.createItem(player, ItemsList.POTION_HEAL_TIER4), 5D);

            Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
            if (dungeon != null) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                    final Block block = event.getClickedBlock();
                    if (openedChests.get(player.getUniqueId()) == null) openedChests.put(player.getUniqueId(), new ArrayList<>());
                    if (block.getType() == Material.CHEST) {
                        if (!openedChests.get(player.getUniqueId()).contains(block)) {
                            Chest chest = (Chest) block.getState();
                            ItemStack random = LuckUtil.getRandomWeighted(drops);
                            chest.open();
                            player.getInventory().addItem(random);
                            player.playSound(block.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);

                            if (random.getItemMeta() != null &&
                                    random.getItemMeta().getDisplayName() != null &&
                                    !Objects.isNull(NBTEditor.getInt(random, "ID"))) {
                                final ItemsList drop = ItemsList.getItemByID(NBTEditor.getInt(random, "ID"));
                                if (drop != ItemsList.NULL)
                                    MessageUtils.broadcastDungeon(dungeon, "&b" + player.getName() + " &7opened a chest and received " + drop.getRarity().getColor() + drop.getName() + " &8(" + drops.get(random) + "%)", MessageUtils.BroadcastType.MESSAGE);
                            }
                            // Display the item
                            ItemDisplay itemDisplay = (ItemDisplay) block.getWorld().spawnEntity(block.getLocation().add(0.5, 1, 0.5), EntityType.ITEM_DISPLAY);
                            itemDisplay.setItemStack(random);
                            itemDisplay.setGravity(false);
                            Transformation st = itemDisplay.getTransformation();
                            float height = 0.7f;
                            st.getScale().set(height, height, height);
                            itemDisplay.setTransformation(st);
                            itemDisplay.setBillboard(Display.Billboard.CENTER);
                            itemDisplay.setCustomName(random.getItemMeta().getDisplayName() + ((random.getAmount() > 1) ? ChatColor.WHITE + " x" + random.getAmount() : ""));
                            itemDisplay.setCustomNameVisible(true);

                            new BukkitRunnable() {
                                int timer = 0;

                                @Override
                                public void run() {
                                    timer++;
                                    if (timer >= 3) {
                                        chest.close();
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(DungeonsSim.getInstance(), 0, 20);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    itemDisplay.remove();
                                }
                            }.runTaskLater(DungeonsSim.getInstance(), 50); // 2.5 seconds = 50 ticks

                            List<Block> newList = openedChests.get(player.getUniqueId());
                            newList.add(block);
                            openedChests.put(player.getUniqueId(), newList);
                        } else {
                            player.sendMessage(ChatColor.RED + "This chest has already been opened.");
                            player.playSound(block.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
                        }
                        event.setCancelled(true);
                    }
                }
            }
        });
        EventManager.on(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            final Location location = event.getFrom();
            final DungeonRoom dungeonRoom = DungeonHandler.getRoomFromLocation(location);
            if (dungeonRoom != null &&
                    dungeonRoom.getCuboid().isIn(location) &&
                    dungeonRoom.getDungeon().getPlayers().contains(player.getUniqueId()) &&
                    dungeonRoom.getTemplate().getName().equals(getName())) {
                //ChatColor cl = ChatColor.BLUE;
                //if (dungeonRoom.getRoomName().equals("ROOM_RARE")) cl = ChatColor.DARK_PURPLE;
                /*player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(cl + getName() + "-" + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() +
                        ", dungeonID: " + dungeonRoom.getDungeon().getID() +
                        ", players in dungeon: " + dungeonRoom.getDungeon().getPlayers().size() +
                        ", players in current room " + dungeonRoom.getWatchers().size()));*/
                //player.sendMessage("You're in: " + dungeonRoom.getRoomName() + "#" + dungeonRoom.getID() + " template: " + dungeonRoom.getTemplate().getName() + " dungeonID: " + dungeonRoom.getDungeon().getID());
                /*List<String> names = new ArrayList<>();
                if (dungeonRoom.getWatchers().size() > 0) {
                    dungeonRoom.getWatchers().forEach(uuid -> {
                        if (Bukkit.getPlayer(uuid) != null)
                            names.add(Bukkit.getPlayer(uuid).getName());
                    });
                }*/
                //player.sendMessage("Players: " + String.join(", ", names));
            }
        });
    }

    /*@Override
    public Map<Cuboid, Double> getBuilds() {
        final Map<Cuboid, Double> map = new HashMap<>();
        final Cuboid room1 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 54),
                new Location(Bukkit.getWorld("world"), -22, 72, 39));
        final Cuboid room2 = new Cuboid(
                new Location(Bukkit.getWorld("world"), -7, 89, 54),
                new Location(Bukkit.getWorld("world"), -22, 72, 39));

        map.put(room1, 50.0);
        map.put(room2, 50.0);
        return map;
    }*/


    @Override
    public MonsterFrequency getMonstersFrequency() {
        MonsterFrequency monsterFrequency = new MonsterFrequency();

        // Add specific room frequencies
        monsterFrequency.addFrequency(UndeadSkeleton.class, this, "ROOM_3", 20.0);
        monsterFrequency.addFrequency(UndeadSkeleton.class, this, "ROOM_1", 20.0);
        monsterFrequency.addFrequency(UndeadSkeleton.class, this, "ROOM_6", 30.0);
        monsterFrequency.addFrequency(UndeadWarrior.class, this, "ROOM_6", 100.0, 1);
        monsterFrequency.addFrequency(Tarantula.class, this, "ROOM_1", 50.0);
        monsterFrequency.addFrequency(Tarantula.class, this, "ROOM_6", 30.0);
        monsterFrequency.addFrequency(UndeadFrozen.class, 15.0,this);
        monsterFrequency.addFrequency(Undead.class, 35.0,this);
        monsterFrequency.addFrequency(UndeadKnight.class, 10.0,this);

        /*Map<Class<? extends CustomMob>, List<SpawnInfo>> monsters = new HashMap<>();

        monsters.put(Undead.class, 35.0);
        monsters.put(UndeadFrozen.class, 35.0);
        monsters.put(UndeadSkeleton.class, 20.0);
        monsters.put(UndeadKnight.class, 10.0);*/
        //monsters.put(CustomNonHostileMob.class, 50.0);
        return monsterFrequency;
    }
}
