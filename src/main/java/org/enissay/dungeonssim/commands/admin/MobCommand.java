package org.enissay.dungeonssim.commands.admin;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import org.apache.http.util.Args;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.CustomMob;
import org.enissay.dungeonssim.handlers.EntitiesHandler;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@Command(name = "mob")
public class MobCommand {
    private void sendInfo(final CommandSender player, final String msg) {
        sendMessage(player, "&7" + msg);
    }

    private void sendNormal(final CommandSender player, final String msg) {
        sendMessage(player, "&a" + msg);
    }

    private void sendError(final CommandSender player, final String msg) {
        sendMessage(player, "&c" + msg);
    }

    private void sendMessage(final CommandSender player, final String msg) {
        player.sendMessage(msg.replace('&', '§'));
    }

    @Execute(name = "spawn")
    void spawn(@Context CommandSender sender, @Arg int lvl, @Join String mobName) {
        final Player player = (Player)sender;
        final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfileByName(player.getName());
        if (sender instanceof Player && ProfilesHandler.findProfileByName(sender.getName()) == null) sender.sendMessage(ChatColor.RED + "Your profile does not exist in the database.");
        else if (sender instanceof Player && !Rank.hasPermissionsOf(((Player) sender).getPlayer(), Rank.ADMIN)) sender.sendMessage(ChatColor.RED + "You do not have enough permissions.");
        else if (dungeonPlayer == null) sender.sendMessage(ChatColor.RED + "This player does not exist in the database.");
        else {

            CustomMob cm = EntitiesHandler.getCustomMobs().stream().filter(customMob -> customMob.getEntityCustomName().equalsIgnoreCase(mobName)).findAny().orElse(null);
            if (cm == null) {
                sendError(player, "This mob does not exist");
                return;
            }else {
                try {
                    Constructor<? extends CustomMob> constructor = cm.getClass().getConstructor(Location.class, int.class, double.class);
                    constructor.newInstance(player.getLocation(), lvl, 1);
                    sendNormal(player, "Spawned " + cm.getEntityCustomName() + " at level " + lvl);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }
            //new TestCreature(player.getLocation().add(0, 2, 0));
            /*new CustomHostileMob(player.getLocation().add(0, 2, 0));
            new CustomNonHostileMob(player.getLocation().add(0, 2, 0));*/
            //EntitiesHandler.addEntity(m);
            /*Mob mob = (Mob) player.getWorld().spawnEntity(player.getLocation(), EntityType.SHEEP);

            test(player, mob);*/
        }
    }

    public class TestCreature extends SkeletonHorse implements CustomMob {
        public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "RacistCow");

        public TestCreature(Location loc) {
            super(EntityType.SKELETON_HORSE, ((CraftWorld) loc.getWorld()).getHandle());
            this.setPosRaw(loc.getX(), loc.getY(), loc.getZ());
            this.setBaby(false);
            this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
            this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F));
            this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

            this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));

            this.getBukkitEntity().getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);

            EntitiesHandler.addEntity(this);

            this.persist = true;
            ((CraftWorld) loc.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }

        @Override
        protected void dropFromLootTable(DamageSource source, boolean causedByPlayer) {
            //super.dropFromLootTable(source, causedByPlayer);
            if (this.level instanceof ServerLevel) {
                getDrops().forEach(drop -> {
                    net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(drop);
                    ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), nmsItemStack);
                    this.level.addFreshEntity(itemEntity);
                });
            }
        }

        @Override
        public AttributeMap getAttributes() {
            return new AttributeMap(Zombie.createAttributes()
                    .add(Attributes.ATTACK_DAMAGE, 4.0)
                    .add(Attributes.FOLLOW_RANGE, 35.0)
                    .add(Attributes.MAX_HEALTH, 40000000)
                    .add(Attributes.MOVEMENT_SPEED, .4)
                    .build());
        }

        @Override
        protected @Nullable SoundEvent getAmbientSound() {
            return SoundEvents.WITHER_AMBIENT;
        }

        @Override
        protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
            return SoundEvents.WITHER_SKELETON_HURT;
        }

        @Override
        protected @Nullable SoundEvent getDeathSound() {
            return SoundEvents.WITHER_SKELETON_DEATH;
        }

        @Override
        public String getEntityCustomName() {
            return "Nigga";
        }

        @Override
        public boolean shouldDropExperience() {
            return false;
        }

        @Override
        public Set<ItemStack> getDrops() {
            Set<ItemStack> items = new HashSet<>();
            ItemStack a = new ItemStack(Material.COAL_BLOCK, 5);
            items.add(a);
            return items;
        }

        @Override
        public Entity getNMSEntity() {
            return this;
        }
    }

    /*public void test(Player player, Mob mob) {
        try {
            EntityBrain brain = BukkitBrain.getBrain(mob);

            // GoalSelector AI
            EntityAI goal = brain.getGoalAI();

            // TargetSelector AI
            EntityAI target = brain.getTargetAI();

            PathfinderMeleeAttack melee = new PathfinderMeleeAttack((Creature) mob);
            PathfinderLookAtEntity<Player> look = new PathfinderLookAtEntity<>(mob, Player.class);
            look.setRange(8F);
            PathfinderRandomLook rLook = new PathfinderRandomLook(mob);
            melee.setSpeedModifier(1);
            PathfinderNearestAttackableTarget<Player> path = new PathfinderNearestAttackableTarget<>(mob, Player.class);

            goal.put(melee, 1);
            goal.put(look, 2);
            goal.put(rLook, 3);

            target.put(path, 1);

            sendNormal(player, "Successfully done.");
        }catch (AssertionError | Exception e) {
            sendError(player, "An error has occurred: " + e.getCause().getMessage());
        }
    }*/

}
