package org.enissay.dungeonssim.listeners;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.handlers.ScoreboardHandler;
import org.enissay.dungeonssim.profiles.BuilderMode;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;

import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SafeZoneListeners implements Listener {
    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
        event.setCancelled(isInSafeZone((Player)event.getEntity()));
    }

    @EventHandler
    public void onExplode(final EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpread(final BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(final BlockBreakEvent event) {
        event.setCancelled(!BuilderMode.canBuild(event.getPlayer()));
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        event.setCancelled(!BuilderMode.canBuild(event.getPlayer()));
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player.getLocation().getY() < 5) player.performCommand("suicide");
    }

    @EventHandler
    public void onPortal(final PortalCreateEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortal(final PlayerChangedWorldEvent event) {
        event.getPlayer().performCommand("suicide");
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
            event.setCancelled(isInSafeZone((Player)event.getDamager()));
        else if (event.getEntity() instanceof Player)
            event.setCancelled(isInSafeZone((Player)event.getEntity()));
    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        event.setCancelled((isInSafeZone(event.getPlayer()) && event.getAction() != Action.RIGHT_CLICK_AIR && event.getClickedBlock() != null && (event.getClickedBlock().getType() != Material.ENDER_CHEST)) && !BuilderMode.canBuild(event.getPlayer()));
    }

    @EventHandler
    public void onConsuming2(final PlayerItemConsumeEvent event) {
        /*final ItemStack item = event.getItem();
        if (item != null && !Objects.isNull(NBTEditor.getInt(item, "ID")) && NBTEditor.getInt(item, "ID") == ItemsList.GAPPLE.getId()) {
            final Player player = event.getPlayer();
            final double amount = (5 * player.getMaxHealth()) / 100;
            if (amount + player.getHealth() <= player.getMaxHealth()) {
                player.setHealth(amount + player.getHealth());
            }
            else player.setHealth(player.getMaxHealth());
        }*/
        //event.setCancelled(isInSafeZone(event.getPlayer()));
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        if (!isInSafeZone(event.getPlayer()) && !event.getPlayer().isSneaking()) event.setCancelled(true);
        else event.setCancelled(isInSafeZone(event.getPlayer()) && !BuilderMode.canBuild(event.getPlayer()));
    }

    @EventHandler
    public void onPickup(final EntityPickupItemEvent event) {
        event.setCancelled(isInSafeZone((Player) event.getEntity()) && !BuilderMode.canBuild((Player) event.getEntity()));
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.teleport(DungeonsSim.getInstance().getSpawnLocation());
        player.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
        player.setFallDistance(0.0F);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setHealth(player.getMaxHealth());
        player.setHealthScale(20.0D);
        player.setWalkSpeed(1f);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);

        player.getInventory().setItemInOffHand(null);

        //player.getInventory().clear();

        final DungeonPlayer minefortPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
        if (minefortPlayer == null) {
            ProfilesHandler.createProfile(player, Rank.PLAYER);
            //player.getInventory().addItem(ItemsList.createItem(player, ItemsList.STALWART_BLADE));
            //GIVE DEFAULT STUFF LATER HERE
        }

        try {
            ProfilesHandler.saveProfiles();
        } catch (IOException e) {
            System.out.println("SAVING PROFILES FAILED AAAAAAH!!!!");
            e.printStackTrace();
        }

        if (minefortPlayer != null && !Rank.isStaff(player))
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&6" + player.getName() + " &ejoined the server."));
        else {
            event.setJoinMessage(null);
            String prefix = "&b&lSTAFF &8- &r";
            Bukkit.getOnlinePlayers().forEach(players -> {
                if (minefortPlayer != null && Rank.isStaff(players)) players.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&c" + player.getName() + " &ejoined the server."));
            });
        }

        ScoreboardHandler.update();
        BPlayerBoard board = Netherboard.instance().createBoard(player, ScoreboardHandler.getScoreboard(), "§6§LEEEEEEEE");
        board.setName(ChatColor.GREEN.toString() + ChatColor.BOLD + "DUNGEONSSIM" + ChatColor.GRAY + " - " + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size());

        ScoreboardHandler.render(player, board);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event){
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onLeave(final PlayerQuitEvent event){
        try {
            final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(event.getPlayer().getUniqueId().toString());
            if (dungeonPlayer != null && !Rank.isStaff(event.getPlayer()))
                event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&6" + event.getPlayer().getName() + " &eleft the server."));
            else {
                event.setQuitMessage(null);
                String prefix = "&b&lSTAFF &8- &r";
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (Rank.isStaff(players)) players.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&c" + event.getPlayer().getName() + " &eleft the server."));
                });
            }
            dungeonPlayer.setLastOnline(Date.from(Instant.now()));
            ProfilesHandler.updateProfile(event.getPlayer().getUniqueId().toString(), dungeonPlayer);
            ProfilesHandler.saveProfiles(); // MAAAAAAAYBE NOT SO PERFORMANT
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isInSafeZone(final Player player) {
        return !DungeonHandler.isPlayerInADungeon(player);
    }
}
