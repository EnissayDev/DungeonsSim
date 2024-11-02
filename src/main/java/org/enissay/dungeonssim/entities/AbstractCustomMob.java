package org.enissay.dungeonssim.entities;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.abilities.Ability;
import org.enissay.dungeonssim.handlers.EntitiesHandler;
import org.enissay.dungeonssim.utils.FormatUtil;
import org.enissay.dungeonssim.utils.LuckUtil;

import java.util.*;

public abstract class AbstractCustomMob extends PathfinderMob implements CustomMob {

    private final NamespacedKey key;
    private final Location spawnLocation;
    private double superiorChance = 0.1;
    private boolean superior = false;
    private int mobLevel;
    private ChatColor color = ChatColor.GREEN;
    private double healthMultiplier;

    private List<Ability> abilities = new ArrayList<>();
    private int currentAbilityIndex = 0;
    private boolean useRandomAbilities;
    private boolean immune;
    private TextDisplay aboveName;

    protected AbstractCustomMob(EntityType<? extends PathfinderMob> entityType, Location spawnLocation, NamespacedKey key, ChatColor color, double superiorChance, int level, double healthMultiplier) {
        super(entityType, ((CraftWorld) spawnLocation.getWorld()).getHandle());
        this.spawnLocation = spawnLocation;
        this.key = key;
        this.superiorChance = superiorChance;
        this.healthMultiplier = (healthMultiplier > 0 ? healthMultiplier : 1);
        this.useRandomAbilities = false;
        this.color = color;
        this.mobLevel = level;

        if (getCustomAttributes() != null)
        getCustomAttributes().forEach((att, val) -> {
            getAttribute(att).setBaseValue(val);
        });

        AttributeInstance maxHealth = getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackDamage = getAttribute(Attributes.ATTACK_DAMAGE);
        if (maxHealth != null && attackDamage != null) {
            double HP = (9.09 * Math.pow(mobLevel, 2) + 91 * mobLevel) * this.healthMultiplier;
            //double damage = (15 * Math.pow(mobLevel, 1.4));
            double damage = Math.log(15+(Math.pow(level, 15)/Math.pow(10, 6)));
            maxHealth.setBaseValue(HP);
            attackDamage.setBaseValue(damage);
        }

        if (!isBoss()) {
            Random random = new Random();

            if (random.nextDouble() < superiorChance) {
                superior = true;
                adjustAttributes();
            }
        }
        this.setHealth((float) getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        this.setPosRaw(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
        this.getBukkitEntity().getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        ((LivingEntity)this.getBukkitEntity()).setRemoveWhenFarAway(false);//Very important
        EntitiesHandler.addEntity(this);
        this.getBukkitEntity().setCustomNameVisible(true);
        this.persist = true;
        ((CraftWorld) spawnLocation.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (immune) {
            return false; // Immune to damage during phase transitions
        }
        return super.hurt(source, amount);
    }

    public void setImmune(boolean immune) {
        this.immune = immune;

        if (immune) {
            aboveName = (TextDisplay) this.getBukkitEntity().getWorld().spawnEntity(
                    this.getBukkitEntity().getLocation().add(0, 2.5, 0), org.bukkit.entity.EntityType.TEXT_DISPLAY);
            aboveName.setText(ChatColor.BOLD + "IMMUNE");
            // Apply a glowing effect for critical hits
            aboveName.setCustomNameVisible(false);
            aboveName.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
            aboveName.setPersistent(false);
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!immune) {
                    aboveName.remove();
                    //aboveName = null;
                    cancel();
                }else {
                    aboveName.teleport(AbstractCustomMob.this.getBukkitEntity().getLocation().add(0, 2.5, 0));
                }
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, 0);
    }

    public boolean isImmune() {
        return immune;
    }

    public void setUseRandomAbilities(boolean useRandomAbilities) {
        this.useRandomAbilities = useRandomAbilities;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public void clearAbilities() {
        this.abilities.clear();
    }

    public void useNextAbility() {
        if (abilities.isEmpty() || !this.isAlive() || isImmune()) return;

        net.minecraft.world.entity.LivingEntity target = this.getTarget();
        if (target != null) {
            Ability ability;
            if (useRandomAbilities) {
                ability = abilities.get(random.nextInt(abilities.size()));
            } else {
                ability = abilities.get(currentAbilityIndex);
            }

            double distanceToTarget = this.distanceToSqr(target);
            if (distanceToTarget <= ability.getRange() * ability.getRange()) {
                if (ability.isReady()) {
                    ability.useAbility(this, target);
                    if (!useRandomAbilities) {
                        currentAbilityIndex = (currentAbilityIndex + 1) % abilities.size();
                    }
                }
            }
        }
    }

    public void updateName() {
        final ChatColor color = getColor();
        if (!isSuperior()) {
            this.getBukkitEntity().setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + getMobLevel() + "] " + color + getEntityCustomName() + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(getHealth()) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(getMaxHealth()) + "❤");
        }else {
            this.getBukkitEntity().setCustomName(ChatColor.DARK_GREEN + "[Lvl. " + getMobLevel() + "] " + ChatColor.DARK_PURPLE + "Superior ✯ " + ChatColor.LIGHT_PURPLE + getEntityCustomName() + ChatColor.DARK_GRAY + " → " + ChatColor.LIGHT_PURPLE + (FormatUtil.format(getHealth())) + ChatColor.GRAY + "/" + ChatColor.DARK_PURPLE + (FormatUtil.format(getMaxHealth()) + "❤"));
        }
    }

    public void sendDialoguesWithDelay(List<net.minecraft.world.entity.LivingEntity> targets, String[] dialogues, long dialogueInterval, Sound sound, float minPitch, float maxPitch) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < dialogues.length) {
                    String message = dialogues[index];
                    AbstractCustomMob.this.sendDialogue(targets, message, sound, minPitch, maxPitch);
                    index++;
                } else
                    this.cancel();
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, dialogueInterval);
    }

    public void sendDialogue(List<net.minecraft.world.entity.LivingEntity> targets, String msg, Sound sound, float minPitch, float maxPitch) {
        if (this.isAlive()) {
            targets.stream().map(Entity::getBukkitEntity).forEach(craftEntity -> {
                craftEntity.sendMessage(ChatColor.translateAlternateColorCodes('&', getColor() + getEntityCustomName() + ChatColor.GRAY + ": " + ChatColor.RESET + msg));
                if (craftEntity instanceof Player player && sound != null) {
                    Random rand = new Random();
                    player.playSound(player.getLocation(), sound, 1, rand.nextFloat() * (maxPitch - minPitch) + minPitch);
                }
            });
        }
    }

    @Override
    public void tick() {
        super.tick();
        useNextAbility();
    }

    private void adjustAttributes() {
        AttributeInstance maxHealth = getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() * 1.5);
        }

        AttributeInstance movementSpeed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(movementSpeed.getBaseValue() * 1.25);
        }
    }

    public int getMobLevel() {
        return mobLevel;
    }

    public ChatColor getColor() {
        return color;
    }

    public double getSuperiorChance() {
        return superiorChance;
    }

    public boolean isSuperior() {
        return superior;
    }

    public abstract Map<Attribute, Double> getCustomAttributes();

    @Override
    protected void dropFromLootTable(DamageSource source, boolean causedByPlayer) {
        if (this.level instanceof ServerLevel) {
            getDrops().forEach(drop -> {
                net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(drop);
                ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), nmsItemStack);
                this.level.addFreshEntity(itemEntity);
            });
        }
    }

    public void equipItem(EquipmentSlot slot, org.bukkit.inventory.ItemStack item) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        this.setItemSlot(slot, nmsItem);
    }

    public void applyCustomHead() {
        if (getCustomHeads() == null || getCustomHeads().length == 0) return;

        Map<String, Double> map = new HashMap<>();
        for (String customHead : getCustomHeads())
            map.put(customHead, (double)1/getCustomHeads().length * 100);
        String val = LuckUtil.getRandomWeighted(map);
        org.bukkit.inventory.ItemStack skull = NBTEditor.getHead("http://textures.minecraft.net/texture/" + val);
        ItemStack nmsSkull = CraftItemStack.asNMSCopy(skull);
        this.setItemSlot(EquipmentSlot.HEAD, nmsSkull);
    }

    public double getHealthMultiplier() {
        return healthMultiplier;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false; // Prevents collision with other entities
    }

    @Override
    public boolean isPushable() {
        return false; // Prevents being pushed by other entities
    }

    @Override
    public boolean canBeCollidedWith() {
        return false; // Prevents other entities from colliding with this entity
    }

    @Override
    public void push(double x, double y, double z) {
        // No-op to prevent being pushed
    }

    @Override
    public void push(Entity entity) {
        // No-op to prevent being pushed
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public Entity getNMSEntity() {
        return this;
    }
}