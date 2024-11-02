package org.enissay.dungeonssim.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.events.DungeonFinishEvent;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.party.DungeonPartyStatus;
import org.enissay.dungeonssim.dungeon.party.DungeonRole;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.PartyHandler;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.utils.FormatUtil;
import org.enissay.dungeonssim.utils.LuckUtil;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.util.*;

public class DungeonListener implements Listener {

    @EventHandler
    public void onDungeonFinish(DungeonFinishEvent event) {
        final Dungeon dungeon = event.getDungeon();
        final int progression = dungeon.getProgression();
        final DungeonParty party = event.getParty();
        if (event.wasSuccessful()) {
            final Map<ItemsList, Double> rewards = new HashMap<>();
            int rewardsCount = 2;

            if (progression >= 70 && progression <= 99) rewardsCount++;
            if (progression >= 100) rewardsCount++;

            //WEAPONS - 62.25
            rewards.put(ItemsList.BRONZE_AXE, 20D);
            rewards.put(ItemsList.IRON_BROADSWORD, 15D);
            rewards.put(ItemsList.STEEL_MACE, 15D);
            rewards.put(ItemsList.GLADIATOR_GREATSWORD, 5D);
            rewards.put(ItemsList.BATTLE_HAMMER, 5D);
            rewards.put(ItemsList.DRAGON_SLAYER, 1D);
            rewards.put(ItemsList.THUNDER_AXE, 1D);
            rewards.put(ItemsList.EXCALIBUR, .25D);

            //ARMORS
            rewards.put(ItemsList.KINGS_HELMET, .5D);
            rewards.put(ItemsList.KINGS_CHESTPLATE, .5D);
            rewards.put(ItemsList.KINGS_LEGGINGS, .5D);
            rewards.put(ItemsList.KINGS_BOOTS, .5D);//2%

            rewards.put(ItemsList.LEATHER_HELMET, 5D);
            rewards.put(ItemsList.LEATHER_CHESTPLATE, 5D);
            rewards.put(ItemsList.LEATHER_LEGGINGS, 5D);
            rewards.put(ItemsList.LEATHER_BOOTS, 5D);

            rewards.put(ItemsList.CHAINMAIL_HELMET, 3.75D);
            rewards.put(ItemsList.CHAINMAIL_CHESTPLATE, 3.75D);
            rewards.put(ItemsList.CHAINMAIL_LEGGINGS, 3.75D);
            rewards.put(ItemsList.CHAINMAIL_BOOTS, 3.75D);

            rewards.put(ItemsList.PALADINS_HELMET, 2.25D);
            rewards.put(ItemsList.PALADINS_CHESTPLATE, 2.25D);
            rewards.put(ItemsList.PALADINS_LEGGINGS, 2.25D);
            rewards.put(ItemsList.PALADINS_BOOTS, 2.25D);

            rewards.put(ItemsList.TITANS_HELMET, 1.4375D);
            rewards.put(ItemsList.TITANS_CHESTPLATE, 1.4375D);
            rewards.put(ItemsList.TITANS_LEGGINGS, 1.4375D);
            rewards.put(ItemsList.TITANS_BOOTS, 1.4375D);

            /*rewards.put(ItemsList.STEEL_WARHAMMER, 10D);
            rewards.put(ItemsList.KNIGHTS_SHIELD, 5D);
            rewards.put(ItemsList.BLAZING_LONGSWORD, 2D);

            rewards.put(ItemsList.WARRIOR_HELMET, 10D);
            rewards.put(ItemsList.WARRIOR_CHESTPLATE, 10D);
            rewards.put(ItemsList.WARRIOR_LEGGINGS, 10D);
            rewards.put(ItemsList.WARRIOR_BOOTS, 10D);

            rewards.put(ItemsList.KNIGHTS_HELMET, 5D);
            rewards.put(ItemsList.KNIGHTS_PLATE, 5D);
            rewards.put(ItemsList.KNIGHTS_LEGGINGS, 5D);
            rewards.put(ItemsList.KNIGHTS_BOOTS, 5D);

            rewards.put(ItemsList.HERO_HELMET, 2D);
            rewards.put(ItemsList.HERO_CHESTPLATE, 2D);
            rewards.put(ItemsList.HERO_GREAVES, 2D);
            rewards.put(ItemsList.HERO_BOOTS, 2D);*/

            if (dungeon != null) {
                final LinkedList<Player> players = new LinkedList<>();
                dungeon.getPlayers().forEach((uuid -> {
                    if (Bukkit.getOfflinePlayer(uuid).isOnline())
                        players.add(Bukkit.getPlayer(uuid));
                }));
                int finalRewardsCount = rewardsCount;
                players.forEach(player -> {
                    player.teleport(DungeonsSim.getInstance().getSpawnLocation());
                    for (int i = 0; i < finalRewardsCount; i++)
                        dropItem(player, dungeon, rewards);
                });

                Bukkit.broadcastMessage(ChatColor.GREEN + "[DUNGEON] " + ChatColor.GOLD + Bukkit.getOfflinePlayer(party.getPlayers(DungeonRole.HOST).get(0)).getName() + ChatColor.YELLOW + "'s party " + ChatColor.GRAY + "[" + party.getName() + "] " + ChatColor.YELLOW + "finished a dungeon on " + ChatColor.GREEN + dungeon.getDungeonDifficulty().name() + ChatColor.YELLOW + " difficulty " + ChatColor.DARK_GRAY + "(" + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + dungeon.getTime() + ChatColor.DARK_GRAY + ")");
                MessageUtils.broadcastDungeonSound(dungeon, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        MessageUtils.broadcastDungeonSound(dungeon, Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
                    }
                }.runTaskLater(DungeonsSim.getInstance(), 10);
            }
        }else {
            MessageUtils.broadcastDungeonSound(dungeon, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 2);
            MessageUtils.broadcastDungeon(dungeon, "&c&lOOF...", MessageUtils.BroadcastType.TITLE);
        }
        /*dungeon.getPlayers().forEach(uuid -> {
            if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                Player player = Bukkit.getPlayer(uuid);
                MessageUtils.broadcastDungeon(dungeon, player.getName() + " damage: " + dungeon.getTotalDamageOf(uuid) + " and deaths: " + dungeon.getDeathsOf(uuid), MessageUtils.BroadcastType.MESSAGE);
                player.teleport(DungeonsSim.getInstance().getSpawnLocation());
            }
        });*/

        displayDamageLeaderboard(dungeon);
        if (party != null) {
            party.setStatus(DungeonPartyStatus.LOBBY);
            if (party.getPlayers().size() >= party.getMaxPlayers()) party.setStatus(DungeonPartyStatus.FULL);
        }
        DungeonHandler.removeDungeon(dungeon);
    }

    public void dropItem(Player player, Dungeon dungeon, Map<ItemsList, Double> rewards) {
        final ItemsList drop = LuckUtil.getRandomWeighted(rewards);
        if (drop.getRarity().getId() >= 2) {
            MessageUtils.broadcastDungeon(dungeon, "&b" + player.getName() + " &7dropped " + drop.getRarity().getColor() + drop.getName() + " &8(" + rewards.get(drop) + "%)", MessageUtils.BroadcastType.MESSAGE);
        }
        player.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "+ " + ChatColor.RESET + drop.getRarity().getColor() + drop.getName());
        player.getInventory().addItem(ItemsList.createItem(player, drop));
    }

    public void displayDamageLeaderboard(Dungeon dungeon) {
        // Collect player data
        List<PlayerData> playerDataList = new ArrayList<>();
        dungeon.getPlayers().forEach(uuid -> {
            if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                Player player = Bukkit.getPlayer(uuid);
                double totalDamage = dungeon.getTotalDamageOf(uuid);
                int deaths = dungeon.getDeathsOf(uuid);
                playerDataList.add(new PlayerData(player.getName(), totalDamage, deaths));
            }
        });

        // Sort players by damage in descending order
        playerDataList.sort((p1, p2) -> Double.compare(p2.totalDamage, p1.totalDamage));

        // Display the leaderboard
        MessageUtils.broadcastDungeon(dungeon,"&6Leaderboard:", MessageUtils.BroadcastType.MESSAGE);
        for (int i = 0; i < playerDataList.size(); i++) {
            PlayerData playerData = playerDataList.get(i);
            String message = "&8" + (i + 1) + ". &7" + playerData.name + " &8- &fDamage: &c" + FormatUtil.format(playerData.totalDamage) + "&7, &fDeaths: &d" + playerData.deaths;
            MessageUtils.broadcastDungeon(dungeon, message, MessageUtils.BroadcastType.MESSAGE);
        }

        // Teleport players to spawn
        dungeon.getPlayers().forEach(uuid -> {
            if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                Player player = Bukkit.getPlayer(uuid);
                player.teleport(DungeonsSim.getInstance().getSpawnLocation());
            }
        });
    }

    private class PlayerData {
        String name;
        double totalDamage;
        int deaths;

        public PlayerData(String name, double totalDamage, int deaths) {
            this.name = name;
            this.totalDamage = totalDamage;
            this.deaths = deaths;
        }
    }
    /*@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
        event.setDeathMessage(null);
        final Player player = event.getEntity();
        final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
        if (dungeon != null) {
            int maxDeaths = 3;
            dungeon.addDeath(player.getUniqueId());
            int deaths = dungeon.getDeathsOf(player.getUniqueId());
            final DungeonParty party = PartyHandler.getPartyOf(player.getUniqueId());
            if (party != null) MessageUtils.broadcastParty(party, "&c" + player.getName() + "&7 has died. &8[&c" + deaths + "&8/&c" + maxDeaths + "&8]", MessageUtils.BroadcastType.MESSAGE);
            if (deaths >= maxDeaths)
                Bukkit.getPluginManager().callEvent(new DungeonFinishEvent(dungeon, PartyHandler.getPartyOf(player.getUniqueId()), false));
        }
    }*/
}
