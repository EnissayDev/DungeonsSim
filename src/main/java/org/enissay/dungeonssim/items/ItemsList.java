package org.enissay.dungeonssim.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.PlayerClass;
import org.enissay.dungeonssim.utils.FormatUtil;
import org.enissay.dungeonssim.utils.Mode;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public enum ItemsList implements ItemAttributes {

    ENISSAY_SLAYER(2222, 100, Material.NETHERITE_SWORD, "ENISSAY'S SLAYER AXE RAAAAAAHHH", Rarity.MYTHIC, new String[]{
            "&cWhat a &dmysterious &csword... fr",
            "&7(you're never getting it lol)"},
            false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "999999998,999999999");
            map.put("critical_chance", "100");
            map.put("upgrades", "0");
            map.put("CustomModelData", "1");

            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 0;
            if (action == Action.LEFT_CLICK_AIR) {
                final Location startLocation = player.getEyeLocation();
                final Vector direction = startLocation.getDirection().normalize();
                final double range = 25;
                final double stepSize = 0.5;

                boolean hit = false;
                Location currentLocation = startLocation.clone();

                while (!hit && currentLocation.distance(startLocation) <= range) {
                    // Create a line of particles for the laser
                    player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, currentLocation, 0, direction.getX(), direction.getY(), direction.getZ(), 0);

                    // Check for entities in the path
                    for (Entity entity : player.getWorld().getNearbyEntities(currentLocation, 0.5, 0.5, 0.5)) {
                        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                            LivingEntity target = (LivingEntity) entity;

                            // Apply damage to the target based on the item in hand
                            //double attackDamage = Double.parseDouble(NBTEditor.getString(player.getItemInHand(), "attack_damage")) / 2;
                            target.damage(damage, player);

                            hit = true;
                            break;
                        }
                    }
                    currentLocation.add(direction.clone().multiply(stepSize));
                }
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
            }
            return cooldown;
        }

        @Override
        public Map<Enchantment, Integer> getEnchantments() {
            final Map<Enchantment, Integer> map = Maps.newHashMap();
            map.put(Enchantment.LUCK, 1);
            return map;
        }
    },
    ADMIN_HELMET(2223, 100, Material.LEATHER_HELMET, "Admin Helmet", Rarity.MYTHIC, new String[]{"&fOmg it's an &4Admin"},
            false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            //map.put("attack_damage", "999998,999999");
            map.put("health", "999998,999999");
            map.put("critical_chance", "100");
            map.put("upgrades", "0");

            return map;
        }
    },
    ADMIN_CHESTPLATE(2224, 100, Material.IRON_CHESTPLATE, "Admin Chestplate", Rarity.MYTHIC, new String[]{"&fOmg it's an &4Admin"},
            false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            //map.put("attack_damage", "999998,999999");
            map.put("health", "999998,999999");
            map.put("critical_chance", "100");
            map.put("upgrades", "0");

            return map;
        }
    },
    ADMIN_LEGGINGS(2225, 100, Material.DIAMOND_LEGGINGS, "Admin Leggings", Rarity.MYTHIC, new String[]{"&fOmg it's an &4Admin"},
            false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            //map.put("attack_damage", "999998,999999");
            map.put("health", "999998,999999");
            map.put("critical_chance", "100");
            map.put("upgrades", "0");

            return map;
        }
    },
    ADMIN_BOOTS(2226, 100, Material.GOLDEN_BOOTS, "Admin Boots", Rarity.MYTHIC, new String[]{"&fOmg it's an &4Admin"},
            false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            //map.put("attack_damage", "999998,999999");
            map.put("health", "999998,999999");
            map.put("critical_chance", "100");
            map.put("upgrades", "0");

            return map;
        }
    },
    /*IRON_BROADSWORD(1, 1, Material.IRON_SWORD, "Iron Broadsword", Rarity.COMMON, new String[]{"&7A sturdy iron sword for the novice warrior."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");

            return map;
        }
    },
    STEEL_WARHAMMER(2, 5, Material.STONE_AXE, "Steel Warhammer", Rarity.UNCOMMON, new String[]{"&7Heavy warhammer that deals increased damage to armored foes."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "40,60");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");

            return map;
        }
    },
    KNIGHTS_SHIELD(3, 1, Material.SHIELD, "Knight's Shield", Rarity.UNCOMMON, new String[]{"&7Basic shield providing standard defense."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "5,10");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");

            return map;
        }
    },
    BLAZING_LONGSWORD(4, 10, Material.IRON_SWORD, "Blazing Longsword", Rarity.RARE, new String[]{"i have no idea"}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "70,100");
            map.put("critical_chance", "6");
            map.put("upgrades", "0");

            return map;
        }
    },
    WARRIOR_HELMET(10, 10, Material.LEATHER_HELMET, "Warrior's Helmet", Rarity.COMMON, new String[]{"Basic helmet offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "10,20");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");

            return map;
        }
    },
    WARRIOR_CHESTPLATE(11, 10, Material.LEATHER_CHESTPLATE, "Warrior's Chestplate", Rarity.COMMON, new String[]{"Basic chestplate offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "10,20");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");

            return map;
        }
    },
    WARRIOR_LEGGINGS(12, 10, Material.LEATHER_LEGGINGS, "Warrior's Leggings", Rarity.COMMON, new String[]{"Basic leggings offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "10,20");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");

            return map;
        }
    },
    WARRIOR_BOOTS(13, 10, Material.LEATHER_BOOTS, "Warrior's Boots", Rarity.COMMON, new String[]{"Basic boots offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "10,20");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");

            return map;
        }
    },
    KNIGHTS_HELMET(14, 15, Material.CHAINMAIL_HELMET, "Knight's Helmet", Rarity.UNCOMMON, new String[]{"Sturdy helmet providing enhanced defense."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");

            return map;
        }
    },
    KNIGHTS_PLATE(15, 15, Material.CHAINMAIL_CHESTPLATE, "Knight's Plate", Rarity.UNCOMMON, new String[]{"Sturdy chestplate providing enhanced defense."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");

            return map;
        }
    },
    KNIGHTS_LEGGINGS(16, 15, Material.CHAINMAIL_LEGGINGS, "Knight's Leggings", Rarity.UNCOMMON, new String[]{"Sturdy leggings providing enhanced defense."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");

            return map;
        }
    },
    KNIGHTS_BOOTS(17, 15, Material.CHAINMAIL_BOOTS, "Knight's Boots", Rarity.UNCOMMON, new String[]{"Sturdy boots providing enhanced defense."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");

            return map;
        }
    },
    HERO_HELMET(18, 20, Material.PLAYER_HEAD, "Hero's Helmet", Rarity.RARE, new String[]{"i literally got no idea what to put here."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,70");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");

            return map;
        }
        @Override
        public String getCustomHead() {
            return "189702e343df442ccc3cbca3f3cd045eb9e511b2dadcd542f7ee5c3fb499b46b";
        }
    },
    HERO_CHESTPLATE(19, 20, Material.IRON_CHESTPLATE, "Hero's Chestplate", Rarity.RARE, new String[]{"i literally got no idea what to put here."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,70");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");

            return map;
        }
    },
    HERO_GREAVES(20, 20, Material.IRON_LEGGINGS, "Hero's Greaves", Rarity.RARE, new String[]{"i literally got no idea what to put here."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,70");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");

            return map;
        }
    },
    HERO_BOOTS(21, 20, Material.IRON_BOOTS, "Hero's Boots", Rarity.RARE, new String[]{"i literally got no idea what to put here."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,70");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");

            return map;
        }
    },*/
    RUSTY_SWORD(1, 1, Material.WOODEN_SWORD, "Rusty Sword", Rarity.COMMON, new String[]{"&7A basic and worn-out sword."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    BRONZE_AXE(2, 1, Material.WOODEN_AXE, "Bronze Axe", Rarity.COMMON, new String[]{"&7A sturdy axe for the novice warrior."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    IRON_BROADSWORD(3, 1, Material.IRON_SWORD, "Iron Broadsword", Rarity.UNCOMMON, new String[]{"&7A sturdy iron sword for the novice warrior."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    STEEL_MACE(4, 1, Material.STONE_AXE, "Steel Mace", Rarity.UNCOMMON, new String[]{"&7A heavy mace for stronger attacks."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    GLADIATOR_GREATSWORD(5, 1, Material.DIAMOND_SWORD, "Gladiator's Greatsword", Rarity.RARE, new String[]{"&7A greatsword used by the gladiators.", "&7On left-clicking in the air, the weapon deals &c25% &7of its attack damage", "&7to all nearby enemies within a 5-block radius."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "50,60");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 15;
            if (action == Action.LEFT_CLICK_AIR) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
                double baseDamage = damage*.25;
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).damage(baseDamage, player);
                    }
                }
                return cooldown;
            }
            return -1;
        }
    },
    BATTLE_HAMMER(6, 1, Material.DIAMOND_AXE, "Battle Hammer", Rarity.RARE, new String[]{"&7A heavy hammer for smashing enemies.", "&7On left-clicking in the air, a shockwave emanates from the player", "&7dealing &c30% &7of the weapon's attack damage", "&7and knocking back enemies within a &a5-block radius&7."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "55,65");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 13;
            if (action == Action.LEFT_CLICK_AIR) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1);
                List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).damage(damage*.3, player);
                        //((LivingEntity) entity).setVelocity(new Vector(0, 1, 0));
                        Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5);
                        direction.setY(0.5); // Adds some upward movement to the push
                        ((LivingEntity) entity).setVelocity(direction);
                    }
                }
                return cooldown;
            }
            return -1;
        }
    },
    DRAGON_SLAYER(7, 1, Material.NETHERITE_SWORD, "Dragon Slayer", Rarity.LEGENDARY, new String[]{"&7A sword that deals extra damage to dragons."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    THUNDER_AXE(8, 1, Material.NETHERITE_AXE, "Thunder Axe", Rarity.LEGENDARY, new String[]{"&7An axe that calls down lightning on hit.", "&7Upon clicking on a block, a lightning bolt strikes the targeted block","&7dealing &c40% &7of the weapon's attack damage to nearby enemies."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "75,85");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 25;
            if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
                //Location target = player.getTargetBlock(null, 50).getLocation();
                Location target = event.getClickedBlock().getLocation();
                player.getWorld().strikeLightningEffect(target);
                List<Entity> nearbyEntities = player.getWorld().getNearbyEntities(target, 4, 4, 4).stream().toList();
                double baseDamage = damage * .4;
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).damage(baseDamage, player);
                    }
                }
                return cooldown;
            }
            return -1;
        }
    },
    EXCALIBUR(9, 1, Material.DIAMOND_SWORD, "Excalibur", Rarity.MYTHIC, new String[]{"&7A legendary sword with unmatched power.", "&7On left-clicking in the air, a burst of light heals allies", "&7within a &a5-block radius &7for &c20% &7of their max health","&7and deals &c70% &7of the weapon's attack damage to enemies."}, false, ItemType.WEAPON, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "90,100");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 20;
            if (action == Action.LEFT_CLICK_AIR) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                List<Entity> nearbyEntities = player.getNearbyEntities(10, 10, 10);
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player && !entity.equals(player)) {
                        ((Player) entity).setHealth(Math.min(((Player) entity).getMaxHealth(), ((Player) entity).getHealth() + ((Player) entity).getMaxHealth() * 0.20));
                    } else if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).damage(damage*.7, player);
                    }
                }
                return cooldown;
            }
            return -1;
        }
    },

    LEATHER_HELMET(10, 1, Material.LEATHER_HELMET, "Leather Helmet", Rarity.COMMON, new String[]{"Basic helmet offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },

    LEATHER_CHESTPLATE(11, 1, Material.LEATHER_CHESTPLATE, "Leather Chestplate", Rarity.COMMON, new String[]{"Basic chestplate offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },

    LEATHER_LEGGINGS(12, 1, Material.LEATHER_LEGGINGS, "Leather Leggings", Rarity.COMMON, new String[]{"Basic leggings offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },

    LEATHER_BOOTS(13, 1, Material.LEATHER_BOOTS, "Leather Boots", Rarity.COMMON, new String[]{"Basic boots offering standard protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    CHAINMAIL_HELMET(14, 1, Material.CHAINMAIL_HELMET, "Chainmail Helmet", Rarity.UNCOMMON, new String[]{"Helmet offering medium protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,50");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    CHAINMAIL_CHESTPLATE(15, 1, Material.CHAINMAIL_CHESTPLATE, "Chainmail Chestplate", Rarity.UNCOMMON, new String[]{"Chestplate offering medium protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,50");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    CHAINMAIL_LEGGINGS(16, 1, Material.CHAINMAIL_LEGGINGS, "Chainmail Leggings", Rarity.UNCOMMON, new String[]{"Leggings offering medium protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,50");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    CHAINMAIL_BOOTS(17, 1, Material.CHAINMAIL_BOOTS, "Chainmail Boots", Rarity.UNCOMMON, new String[]{"Boots offering medium protection."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,50");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    PALADINS_HELMET(18, 1, Material.PLAYER_HEAD, "Paladin's Helmet", Rarity.RARE, new String[]{"&7A helmet worn by noble paladins."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "55,65");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public String getCustomHead() {
            return "e2472e513bbc3e47aade2e16e88936aea53697c722efbb60e47cfd725c1a8a34";
        }
    },
    PALADINS_CHESTPLATE(19, 1, Material.GOLDEN_CHESTPLATE, "Paladin's Chestplate", Rarity.RARE, new String[]{"&7A chestplate worn by noble paladins."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "55,65");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }
    },
    PALADINS_LEGGINGS(20, 1, Material.GOLDEN_LEGGINGS, "Paladin's Leggings", Rarity.RARE, new String[]{"&7Leggings worn by noble paladins."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "55,65");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }
    },
    PALADINS_BOOTS(21, 1, Material.GOLDEN_BOOTS, "Paladin's Boots", Rarity.RARE, new String[]{"&7Boots worn by noble paladins."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "55,65");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }
    },
    TITANS_HELMET(22, 1, Material.PLAYER_HEAD, "Titan's Helmet", Rarity.SCATTERED, new String[]{"&7A helmet forged for the titans."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,90");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public String getCustomHead() {
            return "67398eb8447e7bcf13c161122aa83f48a7d54121d4779e993ae1304bd18ac1";
        }
    },
    TITANS_CHESTPLATE(23, 1, Material.IRON_CHESTPLATE, "Titan's Chestplate", Rarity.SCATTERED, new String[]{"&7A chestplate forged for the titans."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,90");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    TITANS_LEGGINGS(24, 1, Material.IRON_LEGGINGS, "Titan's Leggings", Rarity.SCATTERED, new String[]{"&7Leggings forged for the titans."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,90");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    TITANS_BOOTS(25, 1, Material.IRON_BOOTS, "Titan's Boots", Rarity.SCATTERED, new String[]{"&7Boots forged for the titans."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,90");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    KINGS_HELMET(26, 1, Material.PLAYER_HEAD, "King's Helmet", Rarity.LEGENDARY, new String[]{"&7A helmet worn by the greatest kings."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,110");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
        @Override
        public String getCustomHead() {
            return "189702e343df442ccc3cbca3f3cd045eb9e511b2dadcd542f7ee5c3fb499b46b";
        }
    },
    KINGS_CHESTPLATE(27, 1, Material.NETHERITE_CHESTPLATE, "King's Chestplate", Rarity.LEGENDARY, new String[]{"&7A chestplate worn by the greatest kings."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,110");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },
    KINGS_LEGGINGS(28, 1, Material.NETHERITE_LEGGINGS, "King's Leggings", Rarity.LEGENDARY, new String[]{"&7Leggings worn by the greatest kings."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,110");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },
    KINGS_BOOTS(29, 1, Material.NETHERITE_BOOTS, "King's Boots", Rarity.LEGENDARY, new String[]{"&7Boots worn by the greatest kings."}, false, ItemType.ARMOR, PlayerClass.WARRIOR) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,110");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },
    //MAGES
    /*APPRENTICE_WAND(18, 1, Material.STICK, "Apprentice's Wand", Rarity.COMMON, new String[]{"&7A basic wand for novice mages."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    NOVICE_STAFF(19, 1, Material.BLAZE_ROD, "Novice Staff", Rarity.COMMON, new String[]{"&7A staff used by new mages."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    MYSTIC_WAND(20, 1, Material.STICK, "Mystic Wand", Rarity.UNCOMMON, new String[]{"&7A wand imbued with mystic powers."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    ENCHANTED_STAFF(21, 1, Material.BLAZE_ROD, "Enchanted Staff", Rarity.UNCOMMON, new String[]{"&7A staff enchanted with magical powers."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    ARCANE_SCEPTER(22, 1, Material.END_ROD, "Arcane Scepter", Rarity.RARE, new String[]{"&7A scepter filled with arcane energy."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "50,60");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },
    SORCERER_STAFF(23, 1, Material.END_ROD, "Sorcerer's Staff", Rarity.RARE, new String[]{"&7A staff used by powerful sorcerers."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "55,65");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },
    PHOENIX_WAND(24, 1, Material.STICK, "Phoenix Wand", Rarity.LEGENDARY, new String[]{"&7A wand that summons fire and rebirths its wielder."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    FROST_STAFF(25, 1, Material.BLAZE_ROD, "Frost Staff", Rarity.LEGENDARY, new String[]{"&7A staff that controls the power of ice."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "75,85");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    ELDER_WAND(26, 1, Material.END_ROD, "Elder Wand", Rarity.MYTHIC, new String[]{"&7A legendary wand with unmatched magical power."}, false, ItemType.WEAPON, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "90,100");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },
    NOVICE_ROBES_HELMET(27, 1, Material.LEATHER_HELMET, "Novice Robes Hood", Rarity.COMMON, new String[]{"&7Basic hood for novice mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },

    NOVICE_ROBES_CHESTPLATE(28, 1, Material.LEATHER_CHESTPLATE, "Novice Robes", Rarity.COMMON, new String[]{"&7Basic robes for novice mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },

    NOVICE_ROBES_LEGGINGS(29, 1, Material.LEATHER_LEGGINGS, "Novice Robes Leggings", Rarity.COMMON, new String[]{"&7Basic leggings for novice mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },

    NOVICE_ROBES_BOOTS(30, 1, Material.LEATHER_BOOTS, "Novice Robes Boots", Rarity.COMMON, new String[]{"&7Basic boots for novice mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    ADEPT_ROBES_HELMET(31, 1, Material.CHAINMAIL_HELMET, "Adept Robes Hood", Rarity.UNCOMMON, new String[]{"&7Hood for adept mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    ADEPT_ROBES_CHESTPLATE(32, 1, Material.CHAINMAIL_CHESTPLATE, "Adept Robes", Rarity.UNCOMMON, new String[]{"&7Robes for adept mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    ADEPT_ROBES_LEGGINGS(33, 1, Material.CHAINMAIL_LEGGINGS, "Adept Robes Leggings", Rarity.UNCOMMON, new String[]{"&7Leggings for adept mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    ADEPT_ROBES_BOOTS(34, 1, Material.CHAINMAIL_BOOTS, "Adept Robes Boots", Rarity.UNCOMMON, new String[]{"&7Boots for adept mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "35,45");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    MYSTIC_ROBES_HELMET(35, 1, Material.IRON_HELMET, "Mystic Robes Hood", Rarity.RARE, new String[]{"&7Hood for mystic mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,60");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },

    MYSTIC_ROBES_CHESTPLATE(36, 1, Material.IRON_CHESTPLATE, "Mystic Robes", Rarity.RARE, new String[]{"&7Robes for mystic mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,60");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },

    MYSTIC_ROBES_LEGGINGS(37, 1, Material.IRON_LEGGINGS, "Mystic Robes Leggings", Rarity.RARE, new String[]{"&7Leggings for mystic mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,60");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },

    MYSTIC_ROBES_BOOTS(38, 1, Material.IRON_BOOTS, "Mystic Robes Boots", Rarity.RARE, new String[]{"&7Boots for mystic mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "50,60");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },
    ARCANE_ROBES_HELMET(39, 1, Material.DIAMOND_HELMET, "Arcane Robes Hood", Rarity.LEGENDARY, new String[]{"&7Hood for arcane mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },

    ARCANE_ROBES_CHESTPLATE(40, 1, Material.DIAMOND_CHESTPLATE, "Arcane Robes", Rarity.LEGENDARY, new String[]{"&7Robes for arcane mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },

    ARCANE_ROBES_LEGGINGS(41, 1, Material.DIAMOND_LEGGINGS, "Arcane Robes Leggings", Rarity.LEGENDARY, new String[]{"&7Leggings for arcane mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },

    ARCANE_ROBES_BOOTS(42, 1, Material.DIAMOND_BOOTS, "Arcane Robes Boots", Rarity.LEGENDARY, new String[]{"&7Boots for arcane mages."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    GRAND_SORCERER_ROBES_HELMET(43, 1, Material.NETHERITE_HELMET, "Grand Sorcerer Robes Hood", Rarity.MYTHIC, new String[]{"&7Hood for grand sorcerers."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "90,100");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },

    GRAND_SORCERER_ROBES_CHESTPLATE(44, 1, Material.NETHERITE_CHESTPLATE, "Grand Sorcerer Robes", Rarity.MYTHIC, new String[]{"&7Robes for grand sorcerers."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "90,100");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },

    GRAND_SORCERER_ROBES_LEGGINGS(45, 1, Material.NETHERITE_LEGGINGS, "Grand Sorcerer Robes Leggings", Rarity.MYTHIC, new String[]{"&7Leggings for grand sorcerers."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "90,100");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },

    GRAND_SORCERER_ROBES_BOOTS(46, 1, Material.NETHERITE_BOOTS, "Grand Sorcerer Robes Boots", Rarity.MYTHIC, new String[]{"&7Boots for grand sorcerers."}, false, ItemType.ARMOR, PlayerClass.MAGE) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "90,100");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }
    },
    //TANK
    STEEL_CLUB(47, 1, Material.IRON_SWORD, "Steel Club", Rarity.COMMON, new String[]{"&7A heavy club used by novice tanks."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    KNIGHTS_MACE(48, 1, Material.IRON_AXE, "Knight's Mace", Rarity.COMMON, new String[]{"&7A mace wielded by knights."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "1");
            map.put("upgrades", "0");
            return map;
        }
    },
    CRUSADERS_HAMMER(49, 1, Material.IRON_HOE, "Crusader's Hammer", Rarity.UNCOMMON, new String[]{"&7A hammer used by crusaders."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "40,50");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    GUARDIANS_SHIELD(50, 1, Material.SHIELD, "Guardian's Shield", Rarity.UNCOMMON, new String[]{"&7A shield wielded by guardians."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "40,50");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 20;
            if (action == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage(ChatColor.GREEN + "Shield Block Ability Activated!");
                // Add shield block logic
            }
            return cooldown;
        }
    },
    WARLORDS_GREATSWORD(51, 1, Material.DIAMOND_SWORD, "Warlord's Greatsword", Rarity.RARE, new String[]{"&7A massive sword wielded by warlords."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "50,60");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },
    TITANS_GAVEL(52, 1, Material.DIAMOND_AXE, "Titan's Gavel", Rarity.RARE, new String[]{"&7A mighty gavel used by titans."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "55,65");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },
    BASTIONS_MACE(53, 1, Material.DIAMOND_HOE, "Bastion's Mace", Rarity.LEGENDARY, new String[]{"&7A legendary mace wielded by the strongest of tanks."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 30;
            if (action == Action.RIGHT_CLICK_AIR) {
                player.sendMessage(ChatColor.RED + "Unleashing Bastion's Smash!");
                // Add ability logic
            }
            return cooldown;
        }
    },
    COLOSSUS_SHIELD(54, 1, Material.SHIELD, "Colossus Shield", Rarity.LEGENDARY, new String[]{"&7A massive shield used by legendary tanks."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "75,85");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 30;
            if (action == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage(ChatColor.RED + "Activating Colossus Guard!");
                // Add ability logic
            }
            return cooldown;
        }
    },
    TITANIC_WARHAMMER(55, 1, Material.NETHERITE_SWORD, "Titanic Warhammer", Rarity.MYTHIC, new String[]{"&7A legendary warhammer of colossal power."}, false, ItemType.WEAPON, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "90,100");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 40;
            if (action == Action.RIGHT_CLICK_AIR) {
                player.sendMessage(ChatColor.RED + "Unleashing Titanic Slam!");
                // Add ability logic
            }
            return cooldown;
        }
    },
    STEEL_PLATE_HELMET(56, 1, Material.IRON_HELMET, "Steel Plate Helmet", Rarity.COMMON, new String[]{"&7A basic helmet for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "60,80");
            map.put("upgrades", "0");
            return map;
        }
    },

    STEEL_PLATE_CHESTPLATE(57, 1, Material.IRON_CHESTPLATE, "Steel Plate Chestplate", Rarity.COMMON, new String[]{"&7A sturdy chestplate for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "60,80");
            map.put("upgrades", "0");
            return map;
        }
    },

    STEEL_PLATE_LEGGINGS(58, 1, Material.IRON_LEGGINGS, "Steel Plate Leggings", Rarity.COMMON, new String[]{"&7Durable leggings for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "60,80");
            map.put("upgrades", "0");
            return map;
        }
    },

    STEEL_PLATE_BOOTS(59, 1, Material.IRON_BOOTS, "Steel Plate Boots", Rarity.COMMON, new String[]{"&7Reliable boots for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "60,80");
            map.put("upgrades", "0");
            return map;
        }
    },
    IRON_PLATE_HELMET(60, 1, Material.IRON_HELMET, "Iron Plate Helmet", Rarity.UNCOMMON, new String[]{"&7A stronger helmet for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,100");
            map.put("upgrades", "0");
            return map;
        }
    },

    IRON_PLATE_CHESTPLATE(61, 1, Material.IRON_CHESTPLATE, "Iron Plate Chestplate", Rarity.UNCOMMON, new String[]{"&7A stronger chestplate for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,100");
            map.put("upgrades", "0");
            return map;
        }
    },

    IRON_PLATE_LEGGINGS(62, 1, Material.IRON_LEGGINGS, "Iron Plate Leggings", Rarity.UNCOMMON, new String[]{"&7Durable leggings for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,100");
            map.put("upgrades", "0");
            return map;
        }
    },

    IRON_PLATE_BOOTS(63, 1, Material.IRON_BOOTS, "Iron Plate Boots", Rarity.UNCOMMON, new String[]{"&7Reliable boots for tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "80,100");
            map.put("upgrades", "0");
            return map;
        }
    },
    DEFENDERS_HELMET(64, 1, Material.CHAINMAIL_HELMET, "Defender's Helmet", Rarity.RARE, new String[]{"&7A helmet for skilled defenders."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("upgrades", "0");
            return map;
        }
    },

    DEFENDERS_CHESTPLATE(65, 1, Material.CHAINMAIL_CHESTPLATE, "Defender's Chestplate", Rarity.RARE, new String[]{"&7A chestplate for skilled defenders."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("upgrades", "0");
            return map;
        }
    },

    DEFENDERS_LEGGINGS(66, 1, Material.CHAINMAIL_LEGGINGS, "Defender's Leggings", Rarity.RARE, new String[]{"&7Leggings for skilled defenders."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("upgrades", "0");
            return map;
        }
    },

    DEFENDERS_BOOTS(67, 1, Material.CHAINMAIL_BOOTS, "Defender's Boots", Rarity.RARE, new String[]{"&7Boots for skilled defenders."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("upgrades", "0");
            return map;
        }
    },
    TITAN_PLATE_HELMET(68, 1, Material.DIAMOND_HELMET, "Titan Plate Helmet", Rarity.LEGENDARY, new String[]{"&7A helmet for the strongest of tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "130,150");
            map.put("upgrades", "0");
            return map;
        }
    },

    TITAN_PLATE_CHESTPLATE(69, 1, Material.DIAMOND_CHESTPLATE, "Titan Plate Chestplate", Rarity.LEGENDARY, new String[]{"&7A chestplate for the strongest of tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "130,150");
            map.put("upgrades", "0");
            return map;
        }
    },

    TITAN_PLATE_LEGGINGS(70, 1, Material.DIAMOND_LEGGINGS, "Titan Plate Leggings", Rarity.LEGENDARY, new String[]{"&7Leggings for the strongest of tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "130,150");
            map.put("upgrades", "0");
            return map;
        }
    },

    TITAN_PLATE_BOOTS(71, 1, Material.DIAMOND_BOOTS, "Titan Plate Boots", Rarity.LEGENDARY, new String[]{"&7Boots for the strongest of tanks."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "130,150");
            map.put("upgrades", "0");
            return map;
        }
    },
    ETERNAL_DEFENDER_HELMET(72, 1, Material.NETHERITE_HELMET, "Eternal Defender Helmet", Rarity.MYTHIC, new String[]{"&7The helmet of the eternal defender."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "160,180");
            map.put("upgrades", "0");
            return map;
        }
    },

    ETERNAL_DEFENDER_CHESTPLATE(73, 1, Material.NETHERITE_CHESTPLATE, "Eternal Defender Chestplate", Rarity.MYTHIC, new String[]{"&7The chestplate of the eternal defender."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "160,180");
            map.put("upgrades", "0");
            return map;
        }
    },

    ETERNAL_DEFENDER_LEGGINGS(74, 1, Material.NETHERITE_LEGGINGS, "Eternal Defender Leggings", Rarity.MYTHIC, new String[]{"&7The leggings of the eternal defender."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "160,180");
            map.put("upgrades", "0");
            return map;
        }
    },

    ETERNAL_DEFENDER_BOOTS(75, 1, Material.NETHERITE_BOOTS, "Eternal Defender Boots", Rarity.MYTHIC, new String[]{"&7The boots of the eternal defender."}, false, ItemType.ARMOR, PlayerClass.TANK) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "160,180");
            map.put("upgrades", "0");
            return map;
        }
    },
    //ARCHER
    WOODEN_LONGBOW(76, 1, Material.BOW, "Wooden Longbow", Rarity.COMMON, new String[]{"&7A simple wooden bow for novice archers."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");

            return map;
        }
    },
    RECURVE_BOW(78, 1, Material.BOW, "Recurve Bow", Rarity.COMMON, new String[]{"&7A basic recurve bow with improved performance."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "30,40");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");

            return map;
        }
    },
    HUNTERS_BOW(79, 1, Material.BOW, "Hunter's Bow", Rarity.UNCOMMON, new String[]{"&7A bow favored by skilled hunters."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "40,50");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");

            return map;
        }
    },
    SWIFT_LONGBOW(80, 1, Material.BOW, "Swift Longbow", Rarity.UNCOMMON, new String[]{"&7A longbow designed for fast shooting."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "40,50");
            map.put("critical_chance", "6");
            map.put("upgrades", "0");

            return map;
        }
    },
    EAGLE_EYE_BOW(81, 1, Material.BOW, "Eagle Eye Bow", Rarity.RARE, new String[]{"&7A bow with increased range and accuracy."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "50,60");
            map.put("critical_chance", "7");
            map.put("upgrades", "0");

            return map;
        }
    },
    SHARPSHOOTERS_BOW(82, 1, Material.BOW, "Sharpshooter's Bow", Rarity.RARE, new String[]{"&7A bow with exceptional precision."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "55,65");
            map.put("critical_chance", "8");
            map.put("upgrades", "0");

            return map;
        }
    },
    RANGERS_CROSSBOW(83, 1, Material.CROSSBOW, "Ranger's Crossbow", Rarity.LEGENDARY, new String[]{"&7A powerful crossbow for elite rangers."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "70,80");
            map.put("critical_chance", "10");
            map.put("upgrades", "0");

            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 20; // Ability cooldown in seconds
            if (action == Action.RIGHT_CLICK_AIR) {
                player.getWorld().spawnArrow(player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)), player.getLocation().getDirection(), 1.5F, 1.0F);
            }
            return cooldown;
        }
    },

    STORM_ARCHERS_BOW(84, 1, Material.BOW, "Storm Archer's Bow", Rarity.LEGENDARY, new String[]{"&7A bow imbued with the power of storms."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "75,85");
            map.put("critical_chance", "12");
            map.put("upgrades", "0");

            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 25; // Ability cooldown in seconds
            if (action == Action.RIGHT_CLICK_AIR) {
                player.getWorld().strikeLightning(player.getTargetBlock(null, 100).getLocation());
            }
            return cooldown;
        }
    },
    ECLIPSE_BOW(85, 1, Material.BOW, "Eclipse Bow", Rarity.MYTHIC, new String[]{"&7A legendary bow of unparalleled power."}, false, ItemType.WEAPON, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("attack_damage", "90,100");
            map.put("critical_chance", "15");
            map.put("upgrades", "0");

            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 30; // Ability cooldown in seconds
            if (action == Action.RIGHT_CLICK_AIR) {
                player.getWorld().spawnArrow(player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)), player.getLocation().getDirection(), 2.0F, 1.5F);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
            }
            return cooldown;
        }
    },
    RANGERS_HELMET(86, 1, Material.LEATHER_HELMET, "Ranger's Helmet", Rarity.COMMON, new String[]{"&7Basic helmet for archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    RANGERS_CHESTPLATE(87, 1, Material.LEATHER_CHESTPLATE, "Ranger's Chestplate", Rarity.COMMON, new String[]{"&7Basic chestplate for archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    RANGERS_LEGGINGS(88, 1, Material.LEATHER_LEGGINGS, "Ranger's Leggings", Rarity.COMMON, new String[]{"&7Basic leggings for archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },

    RANGERS_BOOTS(89, 1, Material.LEATHER_BOOTS, "Ranger's Boots", Rarity.COMMON, new String[]{"&7Basic boots for archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "30,40");
            map.put("critical_chance", "2");
            map.put("upgrades", "0");
            return map;
        }
    },
    HUNTERS_HELMET(90, 1, Material.CHAINMAIL_HELMET, "Hunter's Helmet", Rarity.UNCOMMON, new String[]{"&7A helmet for seasoned hunters."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "40,50");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },

    HUNTERS_CHESTPLATE(91, 1, Material.CHAINMAIL_CHESTPLATE, "Hunter's Chestplate", Rarity.UNCOMMON, new String[]{"&7A chestplate for seasoned hunters."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "40,50");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },

    HUNTERS_LEGGINGS(92, 1, Material.CHAINMAIL_LEGGINGS, "Hunter's Leggings", Rarity.UNCOMMON, new String[]{"&7Leggings for seasoned hunters."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "40,50");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },

    HUNTERS_BOOTS(93, 1, Material.CHAINMAIL_BOOTS, "Hunter's Boots", Rarity.UNCOMMON, new String[]{"&7Boots for seasoned hunters."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "40,50");
            map.put("critical_chance", "3");
            map.put("upgrades", "0");
            return map;
        }
    },
    SHARPSHOOTERS_HELMET(94, 1, Material.LEATHER_HELMET, "Sharpshooter's Helmet", Rarity.RARE, new String[]{"&7A helmet for expert archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,55");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }
    },

    SHARPSHOOTERS_CHESTPLATE(95, 1, Material.LEATHER_CHESTPLATE, "Sharpshooter's Chestplate", Rarity.RARE, new String[]{"&7A chestplate for expert archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,55");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }
    },

    SHARPSHOOTERS_LEGGINGS(96, 1, Material.LEATHER_LEGGINGS, "Sharpshooter's Leggings", Rarity.RARE, new String[]{"&7Leggings for expert archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,55");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }
    },

    SHARPSHOOTERS_BOOTS(97, 1, Material.LEATHER_BOOTS, "Sharpshooter's Boots", Rarity.RARE, new String[]{"&7Boots for expert archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "45,55");
            map.put("critical_chance", "4");
            map.put("upgrades", "0");
            return map;
        }
    },
    HAWKEYE_HELMET(98, 1, Material.DIAMOND_HELMET, "Hawkeye Helmet", Rarity.LEGENDARY, new String[]{"&7Armor for legendary archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },

    HAWKEYE_CHESTPLATE(99, 1, Material.DIAMOND_CHESTPLATE, "Hawkeye Chestplate", Rarity.LEGENDARY, new String[]{"&7Armor for legendary archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },

    HAWKEYE_LEGGINGS(100, 1, Material.DIAMOND_LEGGINGS, "Hawkeye Leggings", Rarity.LEGENDARY, new String[]{"&7Armor for legendary archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },

    HAWKEYE_BOOTS(101, 1, Material.DIAMOND_BOOTS, "Hawkeye Boots", Rarity.LEGENDARY, new String[]{"&7Armor for legendary archers."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "70,80");
            map.put("critical_chance", "5");
            map.put("upgrades", "0");
            return map;
        }
    },
    PHOENIX_HELMET(102, 1, Material.NETHERITE_HELMET, "Phoenix Helmet", Rarity.MYTHIC, new String[]{"&7Mythical helmet imbued with fire."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("critical_chance", "8");
            map.put("upgrades", "0");
            return map;
        }
    },

    PHOENIX_CHESTPLATE(103, 1, Material.NETHERITE_CHESTPLATE, "Phoenix Chestplate", Rarity.MYTHIC, new String[]{"&7Mythical chestplate imbued with fire."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("critical_chance", "8");
            map.put("upgrades", "0");
            return map;
        }
    },

    PHOENIX_LEGGINGS(104, 1, Material.NETHERITE_LEGGINGS, "Phoenix Leggings", Rarity.MYTHIC, new String[]{"&7Mythical leggings imbued with fire."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("critical_chance", "8");
            map.put("upgrades", "0");
            return map;
        }
    },

    PHOENIX_BOOTS(105, 1, Material.NETHERITE_BOOTS, "Phoenix Boots", Rarity.MYTHIC, new String[]{"&7Mythical boots imbued with fire."}, false, ItemType.ARMOR, PlayerClass.ARCHER) {
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("health", "100,120");
            map.put("critical_chance", "8");
            map.put("upgrades", "0");
            return map;
        }

        @Override
        public double doAbility(Player player, Action action, double damage, PlayerInteractEvent event) {
            double cooldown = 20; // 20-second cooldown
            if (action == Action.RIGHT_CLICK_AIR) {
                player.getWorld().createExplosion(player.getLocation(), 4F);
            }
            return cooldown;
        }
    },*/
    ENDER_PEARL(1000, Material.ENDER_PEARL, "Ender pearl", Rarity.UNCOMMON, new String[]{""}, true, ItemType.OTHER, PlayerClass.NONE),

    POTION_SPEED_TIER1(2030, Material.POTION, "Potion of Speed (Tier I)", Rarity.COMMON, new String[]{Rarity.COMMON.getColor() + "Speed potion that increases movement speed"}, true, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.SPEED;
        }
    },
    POTION_HEAL_TIER1(2000, Material.POTION, "Potion of Healing (Tier I)", Rarity.COMMON, new String[]{Rarity.COMMON.getColor() + "Heal potion that gives +25 hearts"}, true, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.HEAL;
        }
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("heal", "25");

            return map;
        }
    },
    POTION_HEAL_TIER2(2001, Material.POTION, "Potion of Healing (Tier II)", Rarity.COMMON, new String[]{Rarity.COMMON.getColor() + "Heal potion that gives +40 hearts"}, false, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.HEAL;
        }

        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("heal", "40");

            return map;
        }
    },
    POTION_HEAL_TIER3(2002, Material.POTION, "Potion of Healing (Tier III)", Rarity.UNCOMMON, new String[]{Rarity.UNCOMMON.getColor() + "Heal potion that gives +80 hearts"}, false, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.HEAL;
        }
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("heal", "80");

            return map;
        }
    },
    POTION_HEAL_TIER4(2003, Material.POTION, "Potion of Healing (Tier IV)", Rarity.RARE, new String[]{Rarity.RARE.getColor() + "Heal potion that gives +150 hearts"}, false, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.HEAL;
        }
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("heal", "150");

            return map;
        }
    },
    POTION_HEAL_TIER5(2004, Material.POTION, "Potion of Healing (Tier V)", Rarity.SCATTERED, new String[]{Rarity.SCATTERED.getColor() + "Heal potion that gives +350 hearts"}, false, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.HEAL;
        }
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("heal", "350");

            return map;
        }
    },
    POTION_HEAL_TIER6(2005, Material.POTION, "Potion of Healing (Tier VI)", Rarity.LEGENDARY, new String[]{Rarity.LEGENDARY.getColor() + "Heal potion that gives +500 hearts"}, false, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.HEAL;
        }
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("heal", "500");

            return map;
        }
    },
    POTION_HEAL_TIER7(2006, Material.POTION, "Potion of Healing (Tier VII)", Rarity.MYTHIC, new String[]{Rarity.MYTHIC.getColor() + "Heal potion that gives +1000 hearts"}, false, ItemType.OTHER, PlayerClass.NONE) {
        @Override
        public PotionEffectType getPotionEffect() {
            return PotionEffectType.HEAL;
        }
        @Override
        public Map<String, String> getAttributes() {
            Map<String, String> map = new HashMap<>();
            map.put("heal", "1000");

            return map;
        }
    },
    NULL(0, Material.STONE, "Null", Rarity.COMMON, new String[]{""}, false, ItemType.OTHER, PlayerClass.NONE);

    private int id, levelReq;
    private Material mat;
    private String name;
    private Rarity rarity;
    private String[] lore;
    private boolean nature;
    private ItemType itemType;
    private PlayerClass itemClass;

    ItemsList(int id, int levelReq, Material material, String name, Rarity rarity, String[] lore, boolean nature, ItemType itemType, PlayerClass itemClass) {
        this.id = id;
        this.levelReq = levelReq;
        this.mat = material;
        this.name = name;
        this.rarity = rarity;
        this.lore = lore;
        this.nature = nature;
        this.itemType = itemType;
        this.itemClass = itemClass;
    }
    ItemsList(int id, Material material, String name, Rarity rarity, String[] lore, boolean nature, ItemType itemType, PlayerClass itemClass) {
        this.id = id;
        this.levelReq = 1;
        this.mat = material;
        this.name = name;
        this.rarity = rarity;
        this.lore = lore;
        this.nature = nature;
        this.itemType = itemType;
        this.itemClass = itemClass;
    }

    public static ItemsList detectItem(final ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            final ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName() != null) {
                return Arrays.stream(values())
                        .filter(items -> item.getType() == items.getMat())
                        .findFirst()
                        .orElse(NULL);
            }
        }
        return NULL;
    }

    public boolean canPlayerUseItem(Player player) {
        final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
        if (dungeonPlayer != null)
            return dungeonPlayer.getLevel() >= getLevelReq();
        return false;
    }


    public int getLevelReq() {
        return levelReq;
    }

    public PlayerClass getItemClass() {
        return itemClass;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public boolean isNature() {
        return nature;
    }

    public int getId() {
        return id;
    }

    public Material getMat() {
        return mat;
    }

    public String getName() {
        return name;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public String[] getLore() {
        return lore;
    }

    public static Rarity getRarity(int ID) {
        return Arrays.stream(values())
                .filter(item -> item.getId() == ID)
                .map(ItemsList::getRarity)
                .findFirst()
                .orElse(Rarity.COMMON);
    }

    public static ItemsList getItemByName(final String name) {
        return Arrays.stream(values())
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElse(NULL);
    }

    public static ItemsList getItemByID(final int id) {
        return Arrays.stream(values())
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElse(NULL);
    }

    public static ItemStack createItem(final Player player, final ItemsList item) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("owner", player != null ? player.getName() : null);
        attributes.put("obtain_date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date.from(Instant.now())));
        attributes.put("edition_id", Mode.getString(10, Mode.ALPHANUMERIC).toUpperCase());
        return item.toItem(attributes);
    }

    public ItemStack toItem(Map<String, String> attributes) { // CAUSE OF CRASH
        ItemStack item = null;
        if (mat != null && mat != Material.PLAYER_HEAD)
            item = new ItemStack(mat);
        else if (getCustomHead() != null)
            item = NBTEditor.getHead("http://textures.minecraft.net/texture/" + getCustomHead());
        else if (mat != null)
            item = new ItemStack(mat);
        try {
            final LinkedList<String> lore = Lists.newLinkedList();
            NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(item);

            compound.set(id, "tag", "ID");
            final Map<String, String> group = new HashMap<>(); // Create a defensive copy

            attributes.forEach((key, value) -> group.put(value, key));
            getAttributes().forEach((key, value) -> {
                group.put(value, key);
            });

            synchronized (compound) {
                for (String values : group.keySet()) {
                    if (group.get(values).equals("CustomModelData"))
                        compound.set(Integer.valueOf(values), "tag", group.get(values));
                    else
                        compound.set(values, "tag", group.get(values));
                }
            }
            if (!isNature() && mat != null && mat != Material.POTION) {
                lore.add("&8" + itemType.name());
                lore.add("");
            }

            int rollDamage = 0;
            if (NBTEditor.getString(compound, "tag","attack_damage") != null && NBTEditor.getString(compound, "tag","attack_damage").contains(",")) {
                String damage = (NBTEditor.getString(compound, "tag","attack_damage"));
                String[] split = damage.split(",");
                if (split != null && split.length == 2) {
                    int minDamage = Integer.valueOf(split[0]);
                    int maxDamage = Integer.valueOf(split[1]);
                    rollDamage = randomInt(minDamage, maxDamage);
                    compound.set(String.valueOf(rollDamage), "tag", "attack_damage");
                    compound.set("generic.attack_damage", "tag", "AttributeModifiers", NBTEditor.NEW_ELEMENT, "AttributeName");
                    compound.set("generic.attack_damage", "tag", "AttributeModifiers", 0, "Name");
                    compound.set("mainhand", "tag", "AttributeModifiers", 0, "Slot");
                    compound.set(0, "tag", "AttributeModifiers", 0, "Operation");
                    compound.set(rollDamage, "tag", "AttributeModifiers", 0, "Amount");
                    if (itemClass == PlayerClass.WARRIOR) {
                        compound.set(rollDamage, "tag", "AttributeModifiers", 0, "Amount");
                    }else {
                        compound.set(0, "tag", "AttributeModifiers", 0, "Amount");
                    }
                    compound.set(new int[]{1, 1, 1, 1}, "tag", "AttributeModifiers", 0, "UUID");
                }
            }

            int rollHealth = 0;
            if (NBTEditor.getString(compound, "tag","health") != null && NBTEditor.getString(compound, "tag","health").contains(",")) {
                String defense = (NBTEditor.getString(compound, "tag","health"));
                String[] split = defense.split(",");
                if (split != null && split.length == 2) {
                    int minHealth = Integer.valueOf(split[0]);
                    int maxHealth = Integer.valueOf(split[1]);
                    rollHealth = randomInt(minHealth, maxHealth);
                    compound.set(String.valueOf(rollHealth), "tag", "health");
                }
            }

            /*for (String s : this.lore) {

                lore.add(s.replace("$d", (NBTEditor.getString(compound, "tag","obtain_date") == null ? "" : NBTEditor.getString(compound, "tag", "obtain_date")))
                        .replace("$p", (NBTEditor.getString(compound,"tag","owner") == null ? "" : (ProfilesHandler.findProfileByName(NBTEditor.getString(compound,"tag","owner")) != null ? ProfilesHandler.findProfileByName(NBTEditor.getString(compound,"tag","owner")).getRank().fullPrefix() : Rank.PLAYER.fullPrefix()) + " " + NBTEditor.getString(compound,"tag","owner")))
                        .replace("$e", (NBTEditor.getString(compound,"tag","edition_id") == null ? "" : NBTEditor.getString(compound,"tag","edition_id")))
                        .replace("$ad", (NBTEditor.getString(compound,"tag","attack_damage") == null ? "" : NBTEditor.getString(compound,"tag","attack_damage")))
                        .replace("$cc", (NBTEditor.getString(compound,"tag","critical_chance") == null ? "" : NBTEditor.getString(compound,"tag","critical_chance")))
                        .replace("$u", (NBTEditor.getString(compound,"tag","upgrades") == null ? "" : NBTEditor.getString(compound,"tag","upgrades")))
                        .replace("$i", (NBTEditor.getString(compound,"tag","attack_damage") == null ? "" : NBTEditor.getString(compound,"tag","attack_damage")))
                        .replace('&', '§'));
            }*/
            /*
             ,"&8○ &fAttack damage: &c$ad &c\u2694",
            "&8○ &fCritical Strike Chance: &4$cc% &4\u2623",
            "&8○ &fUpgrades: &e$u§6/§e20 &e\u2692",
            "",
            "&8\u2192 &e&lOWNER&7:&r $p", "", "&8#$e &8- $d"},
             */
            for (String s : this.lore)
                lore.add(replacePlaceholders(s, compound));

            if (NBTEditor.getString(compound, "tag","health") != null) lore.add("&8○ &fHealth: &a+$h❤");
            if (NBTEditor.getString(compound, "tag","upgrades") != null) lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
            if (itemClass != PlayerClass.MAGE && NBTEditor.getString(compound, "tag","attack_damage") != null) lore.add("&8○ &fAttack damage: &c$ad &c\u2694");
            else  if (NBTEditor.getString(compound, "tag","attack_damage") != null) lore.add("&8○ &fMagic damage: &d$ad &d\u2694");
            if (NBTEditor.getString(compound, "tag","critical_chance") != null) lore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");

            /*switch (itemClass) {
                case TANK:
                case WARRIOR:
                    switch (itemType) {
                        case ARMOR:
                            lore.add("&8○ &fHealth: &a+$h❤");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                        case WEAPON:
                            lore.add("&8○ &fAttack damage: &c$ad &c\u2694");
                            lore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                    }
                    break;
                case MAGE:
                    switch (itemType) {
                        case ARMOR:
                            lore.add("&8○ &fHealth: &a+$h❤");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                        case WEAPON:
                            lore.add("&8○ &fMagic damage: &d$ad &5\u2694");
                            lore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                    }
                    break;
            }*/
            if (!isNature() && mat != null && mat != Material.POTION) {
                lore.add("");
                lore.add("&8○ &fItem Class: " + getItemClass().getText());
                lore.add("&8○ &fLevel Required: " + FormatUtil.getLvlText(getLevelReq()));
                lore.add("");
                lore.add("&8\u2192 &e&lOWNER&7:&r $p");
                lore.add("");
                lore.add("&8#$e &8- $d");
            }


            lore.forEach(s -> lore.set(lore.indexOf(s), replacePlaceholders(s, compound)));
            /*for (String s : this.lore)
                lore.add(replacePlaceholders(s, compound));*/

            //lore.add("");
            final ItemStack result = NBTEditor.getItemFromTag(compound);
            /*if (result.getType() == Material.POTION && getPotionEffect() != null) {
                PotionMeta meta = (PotionMeta) result.getItemMeta();
                meta.setMainEffect(getPotionEffect());
                PotionEffect speed = new PotionEffect(getPotionEffect(), (120*1000)/50, 2, false, false, true);
                //PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 1000, 1);
                meta.addCustomEffect(speed, true);
                //potionmeta.addCustomEffect(reg, true);
                meta.setDisplayName(rarity.getColor() + name);

                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.setUnbreakable(true);

                lore.add(rarity.toString());

                meta.setLore(lore);
                result.setItemMeta(meta);
            }else {
                final ItemMeta meta = result.getItemMeta();

                meta.setDisplayName(rarity.getColor() + name);

                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.setUnbreakable(true);

                if (getEnchantments() != null) {
                    getEnchantments().forEach((enchant, level) -> {
                        meta.addEnchant(enchant, level, true);
                    });
                }

                lore.add(rarity.toString());

                meta.setLore(lore);
                result.setItemMeta(meta);
            }*/
            if (result.getType() != Material.POTION) {
                ItemMeta meta = result.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(rarity.getColor() + name);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
                    meta.setUnbreakable(true);
                    applyEnchantments(meta);
                    lore.add(rarity.toString());

                    meta.setLore(lore);
                    result.setItemMeta(meta);
                }
            }else {
                PotionMeta meta = (PotionMeta) result.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(rarity.getColor() + name);

                    meta.setColor(getPotionEffect().getColor());
                    meta.setMainEffect(getPotionEffect());
                    PotionEffect speed = new PotionEffect(getPotionEffect(), 20 * 5 * 60, 1, false, false, true);
                    meta.addCustomEffect(speed, true);

                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
                    meta.setUnbreakable(true);
                    applyEnchantments(meta);
                    lore.add(rarity.toString());

                    meta.setLore(lore);
                    result.setItemMeta(meta);
                }
            }

            return result;
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException: " + e.getMessage());
        }
        return null;
    }

    public ItemStack toItemNoOption(Map<String, String> attributes) { // CAUSE OF CRASH
        ItemStack item = null;
        if (mat != null && mat != Material.PLAYER_HEAD)
            item = new ItemStack(mat);
        else if (getCustomHead() != null)
            item = NBTEditor.getHead("http://textures.minecraft.net/texture/" + getCustomHead());
        else if (mat != null)
            item = new ItemStack(mat);
        try {
            final LinkedList<String> lore = Lists.newLinkedList();
            NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(item);

            compound.set(id, "tag", "ID");
            final Map<String, String> group = new HashMap<>(); // Create a defensive copy

            getAttributes().forEach((key, value) -> {
                if (!key.equalsIgnoreCase("attack_damage") || !key.equalsIgnoreCase("health"))
                    group.put(value, key);
            });
            attributes.forEach((key, value) -> group.put(value, key));

            if (!isNature() && mat != null && mat != Material.POTION)
                lore.add("&8" + itemType.name());
            synchronized (compound) {
                for (String values : group.keySet()) {
                    if (group.get(values).equals("CustomModelData"))
                        compound.set(Integer.valueOf(values), "tag", group.get(values));
                    else
                        compound.set(values, "tag", group.get(values));
                }
            }

            for (String s : this.lore)
                lore.add(replacePlaceholders(s, compound));

            if (!isNature() && mat != null && mat != Material.POTION)
                lore.add("");

            /*for (String s : this.lore)
                lore.add(replacePlaceholders(s, compound));*/
            /*switch (itemClass) {
                case TANK:
                case WARRIOR:
                    switch (itemType) {
                        case ARMOR:
                            lore.add("&8○ &fHealth: &a+$h❤");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                        case WEAPON:
                            lore.add("&8○ &fAttack damage: &c$ad &c\u2694");
                            lore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                    }
                    break;
                case MAGE:
                    switch (itemType) {
                        case ARMOR:
                            lore.add("&8○ &fHealth: &a+$h❤");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                        case WEAPON:
                            lore.add("&8○ &fMagic damage: &d$ad &5\u2694");
                            lore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");
                            lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
                            break;
                    }
                    break;
            }*/

            if (NBTEditor.getString(compound, "tag","health") != null) lore.add("&8○ &fHealth: &a+$h❤");
            if (NBTEditor.getString(compound, "tag","upgrades") != null) lore.add("&8○ &fUpgrades: &e$u§6/§e20 &e\u2692");
            if (itemClass != PlayerClass.MAGE && NBTEditor.getString(compound, "tag","attack_damage") != null) lore.add("&8○ &fAttack damage: &c$ad &c\u2694");
            else  if (NBTEditor.getString(compound, "tag","attack_damage") != null) lore.add("&8○ &fMagic damage: &d$ad &d\u2694");
            if (NBTEditor.getString(compound, "tag","critical_chance") != null) lore.add("&8○ &fCritical Strike Chance: &4$cc% &4\u2623");

            if (!isNature() && mat != null && mat != Material.POTION) {
                lore.add("");
                lore.add("&8○ &fItem Class: " + getItemClass().getText());
                lore.add("&8○ &fLevel Required: " + FormatUtil.getLvlText(getLevelReq()));
                lore.add("");
                lore.add("&8\u2192 &e&lOWNER&7:&r $p");
                lore.add("");
                lore.add("&8#$e &8- $d");
            }

            lore.forEach(s -> lore.set(lore.indexOf(s), replacePlaceholders(s, compound)));

            /*for (String s : this.lore) {
                lore.add(s.replace("$d", (NBTEditor.getString(compound, "tag","obtain_date") == null ? "" : NBTEditor.getString(compound, "tag", "obtain_date")))
                        .replace("$p", (NBTEditor.getString(compound,"tag","owner") == null ? "" : ProfilesHandler.findProfileByName(NBTEditor.getString(compound,"tag","owner")).getRank().fullPrefix() + " " + NBTEditor.getString(compound,"tag","owner")))
                        .replace("$e", (NBTEditor.getString(compound,"tag","edition_id") == null ? "" : NBTEditor.getString(compound,"tag","edition_id")))
                        .replace("$ad", (NBTEditor.getString(compound,"tag","attack_damage") == null ? "" : NBTEditor.getString(compound,"tag","attack_damage")))
                        .replace("$cc", (NBTEditor.getString(compound,"tag","critical_chance") == null ? "" : NBTEditor.getString(compound,"tag","critical_chance")))
                        .replace("$u", (NBTEditor.getString(compound,"tag","upgrades") == null ? "" : NBTEditor.getString(compound,"tag","upgrades")))
                        .replace("$i", "&c??")
                        .replace('&', '§'));
            }*/
            //lore.add("");
            final ItemStack result = NBTEditor.getItemFromTag(compound);
            /*if (result.getType() == Material.POTION && getPotionEffect() != null) {
                PotionMeta meta = (PotionMeta) result.getItemMeta();
                meta.setMainEffect(getPotionEffect());
                PotionEffect speed = new PotionEffect(getPotionEffect(), (120*1000)/50, 2, false, false, true);
                meta.addCustomEffect(speed, true);
                meta.setDisplayName(rarity.getColor() + name);

                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.setUnbreakable(true);

                lore.add(rarity.toString());

                meta.setLore(lore);
                result.setItemMeta(meta);
            }else {
                final ItemMeta meta = result.getItemMeta();

                meta.setDisplayName(rarity.getColor() + name);

                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.setUnbreakable(true);

                if (getEnchantments() != null) {
                    getEnchantments().forEach((enchant, level) -> {
                        meta.addEnchant(enchant, level, true);
                    });
                }

                lore.add(rarity.toString());

                meta.setLore(lore);
                result.setItemMeta(meta);
            }*/
            if (result.getType() != Material.POTION) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(rarity.getColor() + name);
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
                    meta.setUnbreakable(true);
                    lore.add(rarity.toString());

                    meta.setLore(lore);
                    result.setItemMeta(meta);
                }
            }else {
                PotionMeta meta = (PotionMeta) result.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(rarity.getColor() + name);

                    meta.setColor(getPotionEffect().getColor());
                    meta.setMainEffect(getPotionEffect());
                    PotionEffect speed = new PotionEffect(getPotionEffect(), 20 * 5 * 60, 1, false, false, true);
                    meta.addCustomEffect(speed, true);

                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS);
                    meta.setUnbreakable(true);
                    applyEnchantments(meta);
                    lore.add(rarity.toString());

                    meta.setLore(lore);
                    result.setItemMeta(meta);
                }
            }
            return result;
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException: " + e.getMessage());
        }
        return null;
    }

    private String replacePlaceholders(String line, NBTEditor.NBTCompound compound) {
        return line.replace("$d", (NBTEditor.getString(compound, "tag","obtain_date") == null ? "" : NBTEditor.getString(compound, "tag", "obtain_date")))
                .replace("$p", (NBTEditor.getString(compound,"tag","owner") == null ? "" : ProfilesHandler.findProfileByName(NBTEditor.getString(compound,"tag","owner")).getRank().fullPrefix() + " " + NBTEditor.getString(compound,"tag","owner")))
                .replace("$e", (NBTEditor.getString(compound,"tag","edition_id") == null ? "" : NBTEditor.getString(compound,"tag","edition_id")))
                .replace("$ad", (NBTEditor.getString(compound,"tag","attack_damage") == null ? "" : NBTEditor.getString(compound,"tag","attack_damage")))
                .replace("$cc", (NBTEditor.getString(compound,"tag","critical_chance") == null ? "" : NBTEditor.getString(compound,"tag","critical_chance")))
                .replace("$u", (NBTEditor.getString(compound,"tag","upgrades") == null ? "" : NBTEditor.getString(compound,"tag","upgrades")))
                .replace("$h", (NBTEditor.getString(compound,"tag","health") == null ? "" : NBTEditor.getString(compound,"tag","health")))
                .replace('&', '§');
    }

    private void applyEnchantments(ItemMeta meta) {
        if (getEnchantments() != null)
            getEnchantments().forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
    }

    public int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }
}