package org.enissay.dungeonssim.listeners;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftCreature;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.passive.Dummy;
import org.enissay.dungeonssim.gui.InventoryGUI;
import org.enissay.dungeonssim.gui.impl.ClassGUI;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.handlers.ScoreboardHandler;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.profiles.BuilderMode;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.PlayerClass;
import org.enissay.dungeonssim.profiles.Rank;
import org.enissay.dungeonssim.profiles.event.PlayerLevelUpEvent;
import org.enissay.dungeonssim.utils.FormatUtil;
import org.enissay.dungeonssim.utils.MessageUtils;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;

public class SafeZoneListeners implements Listener {
    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            event.setCancelled(isInSafeZone((Player)event.getEntity()));
        else {
            if (event.getEntity() instanceof LivingEntity entity) {
                if (entity instanceof CraftCreature) {
                    CraftCreature craftCreature = (CraftCreature) entity;

                    net.minecraft.world.entity.Entity nmsEntity = craftCreature.getHandle();

                    if (nmsEntity instanceof AbstractCustomMob acm) {
                        if (acm instanceof Dummy) return;
                    }
                }
            }
        }
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
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof LivingEntity entity && !(event.getEntity() instanceof Player)) {
                if (entity instanceof CraftCreature) {
                    CraftCreature craftCreature = (CraftCreature) entity;

                    net.minecraft.world.entity.Entity nmsEntity = craftCreature.getHandle();

                    if (nmsEntity instanceof AbstractCustomMob acm) {
                        if (acm instanceof Dummy) return;
                    }
                }
            }
            event.setCancelled(isInSafeZone((Player) event.getDamager()));
        }
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
        if (/*!isInSafeZone(event.getPlayer()) && */!event.getPlayer().isSneaking()) event.setCancelled(true);
        else event.setCancelled(/*isInSafeZone(event.getPlayer()) && */!BuilderMode.canBuild(event.getPlayer()));
    }

    @EventHandler
    public void onPickup(final EntityPickupItemEvent event) {
        event.setCancelled(isInSafeZone((Player) event.getEntity()) && !BuilderMode.canBuild((Player) event.getEntity()));
    }

    /*@EventHandler
    public void onEquip(final PlayerArmorEquipEvent event) {
        final Player player = event.getPlayer();

        double finalHealth = player.getMaxHealth();
        final ItemStack a = event.getItemStack();
        NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(a);
        if (NBTEditor.getString(compound, "tag","health") != null) {
            int health = Integer.valueOf(NBTEditor.getString(compound, "tag","health"));
            finalHealth+=health;
            player.sendMessage("§dAdded " + health + " to " + (finalHealth-health)  +" so now it's " + finalHealth);
        }

        player.setMaxHealth(finalHealth);
    }

    @EventHandler
    public void onUnequip(final PlayerArmorUnequipEvent event) {
        final Player player = event.getPlayer();

        double finalHealth = player.getMaxHealth();
        final ItemStack a = event.getItemStack();
        NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(a);
        if (NBTEditor.getString(compound, "tag","health") != null) {
            int health = Integer.valueOf(NBTEditor.getString(compound, "tag","health"));
            finalHealth-=health;
            player.sendMessage("§dRemoved " + health + " from " + (finalHealth+health)  +" so now it's " + finalHealth);

        }
        player.setMaxHealth(finalHealth);
    }*/

    @EventHandler
    public void onEquip(final ArmorEquipEvent event) {
        final ItemStack i = event.getNewArmorPiece();
        final Player player = event.getPlayer();
        if (i != null) {
            final ItemsList item = ItemsList.getItemByID(NBTEditor.getInt(i, "ID"));
            final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(event.getPlayer().getUniqueId().toString());
            if (item == null || (item != null && item.getItemClass() == PlayerClass.NONE)) return;
            if (i != null &&
                    item != null &&
                    dungeonPlayer != null &&
                    dungeonPlayer.getPlayerClass() != null &&
                    !dungeonPlayer.getPlayerClass().equals(item.getItemClass()) &&
                    item.canPlayerUseItem(player)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You do not match the item's required class!");
                event.setCancelled(true);
            }else if (i != null && item != null && !item.canPlayerUseItem(player)) {
                event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough level to use this item! (Lvl. " + item.getLevelReq() + " required)");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLvlUp(final PlayerLevelUpEvent event) {
        final Player player = event.getPlayer();
        final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
        final int oldLevel = event.getOldLevel();
        final int newLevel = event.getNewLevel();

        if (dungeon != null)
            MessageUtils.broadcastDungeon(dungeon, "&d" + player.getName() + "&7 leveled up from &a" + oldLevel + " &7to &a" + newLevel, MessageUtils.BroadcastType.MESSAGE);

        if (newLevel > 1 && newLevel % 10 == 0) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Player " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + " achieved level " + FormatUtil.getLvlText(newLevel));
        }
        event.getPlayer().sendTitle(FormatUtil.getLvlText(oldLevel) + ChatColor.GRAY + " → " + FormatUtil.getLvlText(newLevel), "", 0, 30, 10);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.65f);

    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        player.teleport(DungeonsSim.getInstance().getSpawnLocation());
        player.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
        player.setFallDistance(0.0F);
        player.setFoodLevel(20);
        /*player.setMaxHealth(100.0D);
        player.setHealth(100.0D);*/
        player.setHealthScale(40.0D);
        player.getInventory().setItemInOffHand(null);
        //player.setWalkSpeed(.1f);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);

        player.setHealth(player.getMaxHealth());


        /*ItemStack[] armor = player.getInventory().getArmorContents();
        double finalHealth = player.getMaxHealth();
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
        player.setHealth(player.getMaxHealth());*/

        final PotionEffect potionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0);
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            player.addPotionEffect(potionEffect);

        player.getInventory().setItemInOffHand(null);

        //player.getInventory().clear();

        final DungeonPlayer minefortPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
        if (minefortPlayer == null) {
            ProfilesHandler.createProfile(player, Rank.PLAYER);
            player.getInventory().addItem(ItemsList.createItem(player, ItemsList.RUSTY_SWORD));
            player.getInventory().addItem(ItemsList.createItem(player, ItemsList.POTION_HEAL_TIER1));
            player.getInventory().addItem(ItemsList.createItem(player, ItemsList.POTION_SPEED_TIER1));
            //GIVE DEFAULT STUFF LATER HERE
        }else {
            if (minefortPlayer.getPlayerClass() != null && minefortPlayer.getPlayerClass() == PlayerClass.NONE) {
                /*final InventoryGUI inventoryGUI = new ClassGUI();
                DungeonsSim.getInstance().getGuiManager().openGUI(inventoryGUI, player);*/
                minefortPlayer.setPlayerClass(PlayerClass.WARRIOR);
                ProfilesHandler.updateProfile(player.getUniqueId().toString(), minefortPlayer);
            }
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
