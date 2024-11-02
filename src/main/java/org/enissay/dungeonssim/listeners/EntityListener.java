package org.enissay.dungeonssim.listeners;

import com.google.common.collect.Maps;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftCreature;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.entities.hostile.AbstractHostileCustomMob;
import org.enissay.dungeonssim.entities.hostile.boss.AbstractBossMob;
import org.enissay.dungeonssim.handlers.DungeonHandler;
import org.enissay.dungeonssim.handlers.EntitiesHandler;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.utils.FormatUtil;
import org.enissay.dungeonssim.utils.LuckUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EntityListener implements Listener {
    EntityType[] blacklist = new EntityType[]{EntityType.PLAYER, EntityType.ENDER_CRYSTAL,
            EntityType.ARMOR_STAND, EntityType.BLOCK_DISPLAY, EntityType.ITEM_DISPLAY, EntityType.ITEM_FRAME,
            EntityType.DROPPED_ITEM};

    @EventHandler
    public void onMobSpawn(final EntitySpawnEvent event) {
        if (event.getEntity() instanceof Creature &&
                !Arrays.stream(blacklist).collect(Collectors.toList()).contains(event.getEntityType())) {
            final Entity entity = event.getEntity();

            entity.setCustomNameVisible(true);
            entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + 1 + "] " + ChatColor.GREEN + entity.getName() + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(((Creature)entity).getHealth()) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(((Creature)entity).getMaxHealth()) + "❤");

            if (entity instanceof CraftCreature) {
                CraftCreature craftCreature = (CraftCreature) entity;
                net.minecraft.world.entity.Entity nmsEntity = craftCreature.getHandle();

                if (nmsEntity instanceof AbstractCustomMob) {
                    AbstractCustomMob acm = (AbstractCustomMob) nmsEntity;
                    final ChatColor color = acm.getColor();
                    if (!acm.isSuperior()) {
                        entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + acm.getMobLevel() + "] " + color + acm.getEntityCustomName() + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(acm.getHealth()) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(acm.getMaxHealth()) + "❤");
                    }else {
                        entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + acm.getMobLevel() + "] " + ChatColor.DARK_PURPLE + "Superior ✯ " + ChatColor.LIGHT_PURPLE + (acm != null ? acm.getEntityCustomName() : entity.getName()) + ChatColor.DARK_GRAY + " → " + ChatColor.LIGHT_PURPLE + (FormatUtil.format(acm.getHealth())) + ChatColor.GRAY + "/" + ChatColor.DARK_PURPLE + (FormatUtil.format(acm.getMaxHealth()) + "❤"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMobDamage(final EntityDamageByEntityEvent event) {
        Entity rawEntity = event.getEntity();

        boolean isCritical = false;

        LivingEntity entity = (LivingEntity) rawEntity;
        CustomMob cm = null;
        if (EntitiesHandler.exists(rawEntity)) cm = EntitiesHandler.getCMFromEntity(rawEntity);
        double damage = event.getFinalDamage();
        double health = entity.getHealth() + entity.getAbsorptionAmount();

        //Damage handler
        if (event.getDamager() instanceof Player player && !(event.getEntity() instanceof Player)) {
            final ItemStack itemInHand = player.getItemInHand();
            final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
            final ItemsList item = ItemsList.getItemByID(NBTEditor.getInt(itemInHand, "ID"));
            if (itemInHand != null && itemInHand.getType() != Material.AIR && NBTEditor.getString(itemInHand, "attack_damage") != null && dungeonPlayer != null && dungeonPlayer.getPlayerClass() != null && item != null && item.getItemClass() != dungeonPlayer.getPlayerClass() && item.canPlayerUseItem(player)) {
                player.sendMessage(ChatColor.RED + "You do not match the item's required class!");
                damage = 0;
                event.setCancelled(true);
                //event.setDamage(0);
            }
            else if (itemInHand != null &&
                    itemInHand.getType() != Material.AIR &&
                    NBTEditor.getString(itemInHand, "attack_damage") != null &&
                    dungeonPlayer != null && dungeonPlayer.getPlayerClass() != null &&
                    item != null &&
                    (item.getItemClass() != dungeonPlayer.getPlayerClass() && !item.canPlayerUseItem(player) || !item.canPlayerUseItem(player))) {
                player.sendMessage(ChatColor.RED + "You do not have enough level to use this item! (Lvl. " + item.getLevelReq() + " required)");
                damage = 0;
                event.setCancelled(true);
                //event.setDamage(0);
            }
            else if (itemInHand != null && NBTEditor.getString(itemInHand, "attack_damage") != null) {
                //final double baseDamage = Double.parseDouble(NBTEditor.getString(itemInHand, "attack_damage"));
                final double critChance = Double.parseDouble(NBTEditor.getString(itemInHand, "critical_chance"));
                final double upgrades = Double.parseDouble(NBTEditor.getString(itemInHand, "upgrades"));

                // Roll for critical hit
                isCritical = Math.random() < (critChance / 100.0);

                if (isCritical) {
                    // Apply critical damage
                    damage *= 1.5;
                    //damage = baseDamage * 1.5;  // 50% more damage for critical hit
                    damage += upgrades;       // Add the upgrades attribute
                    event.setDamage(damage);  // Set the final damage including upgrades
                }
            }
        }
        // Update the entity's name with health information
        if (damage > 0) {
            health -= damage;

            if (health < 0) {
                health = 0;
            }
        }else health = entity.getHealth();

        if (entity instanceof CraftCreature) {
            CraftCreature craftCreature = (CraftCreature) entity;
            net.minecraft.world.entity.Entity nmsEntity = craftCreature.getHandle();

            if (health == 0 && nmsEntity instanceof AbstractBossMob abm && event.getDamager() instanceof Player player) {
                if (abm.getBossBar().getPlayers().contains(player))
                    abm.getBossBar().removePlayer(player);
            }
            if (nmsEntity instanceof AbstractCustomMob) {

                AbstractCustomMob acm = (AbstractCustomMob) nmsEntity;
                if (health == 0 && event.getDamager() instanceof Player player) {
                    final double coin = random_double(10*Math.pow(acm.getMobLevel(), 2), 20*Math.pow(acm.getMobLevel(), 2));
                    final int exp = (int)random_double(10*Math.pow(acm.getMobLevel(), 1.25), 20*Math.pow(acm.getMobLevel(), 1.25));
                    final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
                    final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
                    if (dungeon != null) dungeon.addDamage(player.getUniqueId(), damage);

                    if (dungeonPlayer != null && dungeonPlayer.getLevel() >= acm.getMobLevel()) {
                        final int playerCoins = dungeonPlayer.getCoins();

                        dungeonPlayer.setCoins((int) (playerCoins+coin));
                        ProfilesHandler.updateProfile(player.getUniqueId().toString(), dungeonPlayer);
                        ProfilesHandler.giveEXP(player.getUniqueId().toString(), exp);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "+" + (int)coin + "     " + ChatColor.DARK_AQUA + " (+" + exp + " EXP)"));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                }

                final ChatColor color = acm.getColor();
                if (!acm.isSuperior()) {
                    entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + acm.getMobLevel() + "] " + color + (cm != null ? cm.getEntityCustomName() : extractEntityName(rawEntity.getName())) + ChatColor.DARK_GRAY + " → " + ChatColor.RED + (FormatUtil.format(health)) + ChatColor.GRAY + "/" + ChatColor.RED + (FormatUtil.format(entity.getMaxHealth()) + "❤"));
                } else {
                    entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + acm.getMobLevel() + "] " + ChatColor.DARK_PURPLE + "Superior ✯ " + ChatColor.LIGHT_PURPLE + (cm != null ? cm.getEntityCustomName() : extractEntityName(rawEntity.getName())) + ChatColor.DARK_GRAY + " → " + ChatColor.LIGHT_PURPLE + (FormatUtil.format(health)) + ChatColor.GRAY + "/" + ChatColor.DARK_PURPLE + (FormatUtil.format(entity.getMaxHealth()) + "❤"));
                }
            } else {
                entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + 1 + "] " + extractEntityName(rawEntity.getName()) + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(health) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(craftCreature.getMaxHealth()) + "❤");
            }
        }
        if (!event.isCancelled())
            displayDamageIndicator(entity, damage, isCritical);  // Critical hit display
    }

    private void displayDamageIndicator(Entity entity, double damage, boolean crit) {
        // Random offsets for X, Y, Z
        double offsetX = random_double(-1.25, 1.25);
        double offsetY = random_double(1.0, 2.7); // Above the entity
        double offsetZ = random_double(-1.25, 1.25);

        TextDisplay textDisplay = (TextDisplay) entity.getWorld().spawnEntity(
                entity.getLocation().add(offsetX, offsetY, offsetZ), EntityType.TEXT_DISPLAY
        );

        // Apply a glowing effect for critical hits
        if (crit) {
            textDisplay.setText(ChatColor.RED + FormatUtil.format(damage) + ChatColor.GOLD + " CRIT!");
        } else {
            textDisplay.setText(ChatColor.RED + FormatUtil.format(damage));
        }
        textDisplay.setCustomNameVisible(false);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setPersistent(false);
        textDisplay.setGlowing(crit);

        // Schedule task to remove the display after a short delay
        new BukkitRunnable() {
            double yOffset = 0;

            @Override
            public void run() {
                if (yOffset >= 2.5) textDisplay.remove();
                Location currentLocation = textDisplay.getLocation();

                yOffset += 0.2;
                textDisplay.teleport(currentLocation.add(0, 0.05, 0));
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, 0);
    }

    /*@EventHandler
    public void onMobHit(final EntityDamageEvent event) {
        Entity rawEntity = event.getEntity();

        if (Arrays.stream(blacklist).toList().contains(rawEntity.getType()) && !EntitiesHandler.exists(rawEntity))
            return;
        LivingEntity entity = (LivingEntity) rawEntity;
        CustomMob cm = null;
        if (EntitiesHandler.exists(rawEntity)) cm = EntitiesHandler.getCMFromEntity(rawEntity);
        double damage = event.getFinalDamage(), health = entity.getHealth() + entity.getAbsorptionAmount();
        if (health > damage) {
            health -= damage;
            if (health < 0) health = 0;

            //String[] splited = rawEntity.getName().split("\\s+");
            if (entity instanceof CraftCreature) {
                CraftCreature craftCreature = (CraftCreature) entity;

                net.minecraft.world.entity.Entity nmsEntity = craftCreature.getHandle();

                if (nmsEntity instanceof AbstractCustomMob) {
                    AbstractCustomMob acm = (AbstractCustomMob) nmsEntity;
                    final ChatColor color = acm.getColor();
                    if (!acm.isSuperior()) {
                        entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + acm.getMobLevel() + "] " + color + (cm != null ? cm.getEntityCustomName() : extractEntityName(rawEntity.getName())) + ChatColor.DARK_GRAY + " → " + ChatColor.RED + (FormatUtil.format(health)) + ChatColor.GRAY + "/" + ChatColor.RED + (FormatUtil.format(entity.getMaxHealth()) + "❤"));
                    }else {
                        entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + acm.getMobLevel() + "] " + ChatColor.DARK_PURPLE + "Superior ✯ " + ChatColor.LIGHT_PURPLE + (cm != null ? cm.getEntityCustomName() : extractEntityName(rawEntity.getName())) + ChatColor.DARK_GRAY + " → " + ChatColor.LIGHT_PURPLE + (FormatUtil.format(health)) + ChatColor.GRAY + "/" + ChatColor.DARK_PURPLE + (FormatUtil.format(entity.getMaxHealth()) + "❤"));
                    }
                }else {
                    entity.setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + 1 + "] " + extractEntityName(rawEntity.getName()) + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(health) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(craftCreature.getMaxHealth()) + "❤");
                }
            }
        }
    }*/
    public String extractEntityName(String entityName) {
        // Define a regex pattern to match "✯ <Optional Spaces> <Text> →"
        String regex = "✯\\s*(.*?)\\s*→";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher with the input entityName
        Matcher matcher = pattern.matcher(entityName);

        // Check if the pattern is found
        if (matcher.find()) {
            // Extract the matched group (text after "✯" and before " → ")
            String extractedName = matcher.group(1).trim();
            return extractedName;
        } else {
            // If "✯" is not found, revert to the previous method
            return extractEntityNameFallback(entityName);
        }
    }

    private String extractEntityNameFallback(String entityName) {
        // Define a regex pattern to match "] <Optional Spaces> <Lvl.> <Optional Spaces> <Text> →"
        String regex = "\\]\\s*(.*?)\\s*→";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher with the input entityName
        Matcher matcher = pattern.matcher(entityName);

        // Check if the pattern is found
        if (matcher.find()) {
            // Extract the matched group (text after "Lvl." and before " → ")
            String extractedName = matcher.group(1).trim();
            return extractedName;
        } else {
            // Return a default name or handle the case where pattern is not found
            return "Unknown";
        }
    }
    /*public String extractEntityName(String entityName) {
        // Define a regex pattern to match "[Lvl. ]" followed by any characters until " → "
        String regex = "\\]\\s*(.*?)\\s*→";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher with the input entityName
        Matcher matcher = pattern.matcher(entityName);

        // Check if the pattern is found
        if (matcher.find()) {
            // Extract the matched group (text between "[Lvl. ]" and " → ")
            String extractedName = matcher.group(1).trim();
            return extractedName;
        } else {
            // Return a default name or handle the case where pattern is not found
            return "Unknown";
        }
    }*/
    public double random_double(double Min, double Max)
    {
        return (Double) (Math.random()*(Max-Min))+Min;
    }
}
