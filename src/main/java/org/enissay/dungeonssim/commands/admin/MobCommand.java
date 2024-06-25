package org.enissay.dungeonssim.commands.admin;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import me.gamercoder215.mobchip.EntityBody;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.EntityAI;
import me.gamercoder215.mobchip.ai.controller.NaturalMoveType;
import me.gamercoder215.mobchip.ai.goal.CustomPathfinder;
import me.gamercoder215.mobchip.ai.goal.PathfinderAvoidEntity;
import me.gamercoder215.mobchip.ai.goal.PathfinderLookAtEntity;
import me.gamercoder215.mobchip.ai.goal.PathfinderMeleeAttack;
import me.gamercoder215.mobchip.ai.goal.target.PathfinderNearestAttackableTarget;
import me.gamercoder215.mobchip.ai.memories.EntityMemory;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;
import org.jetbrains.annotations.NotNull;

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
    void rank(@Context CommandSender sender) {
        final Player player = (Player)sender;
        final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfileByName(player.getName());
        if (sender instanceof Player && ProfilesHandler.findProfileByName(sender.getName()) == null) sender.sendMessage(ChatColor.RED + "Your profile does not exist in the database.");
        else if (sender instanceof Player && !Rank.hasPermissionsOf(((Player) sender).getPlayer(), Rank.ADMIN)) sender.sendMessage(ChatColor.RED + "You do not have enough permissions.");
        else if (dungeonPlayer == null) sender.sendMessage(ChatColor.RED + "This player does not exist in the database.");
        else {
            Mob mob = (Mob) player.getWorld().spawnEntity(player.getLocation(), EntityType.SHEEP);

            test(player, mob);
            //new CustomCreature(player.getLocation());
        }
    }

    public void test(Player player, Mob mob) {
        try {
            EntityBrain brain = BukkitBrain.getBrain(mob);

            // GoalSelector AI
            EntityAI goal = brain.getGoalAI();

            // TargetSelector AI
            EntityAI target = brain.getTargetAI();

            //Pathfinder<Zombie> sqdsds = new PathfinderLookAtEntity<>(mob, Zombie.class);
            PathfinderTest test = new PathfinderTest(mob);
            brain.getController().moveTo(player.getLocation().add(5, 0, 7));
            brain.getController().naturalMoveTo(5, 0, 7, NaturalMoveType.SELF); // Move +1 in the X direction, +3 in the Z direction
            goal.put(test, 0);
            target.put(test, 0);

            sendNormal(player, "Successfully done.");
        }catch (AssertionError | Exception e) {
            sendError(player, "An error has occurred: " + e.getCause().getMessage());
        }
    }

    /*private class CustomCreature extends Sheep {

        public CustomCreature(Location location) {
            super(net.minecraft.world.entity.EntityType.SHEEP, ((CraftWorld) location.getWorld()).getHandle());
            this.setPos(location.getX(), location.getY(), location.getZ());
            ((CraftWorld) location.getWorld()).addEntityToWorld(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

            this.initPathfinderGoals();
        }
        private void initPathfinderGoals() {
            this.targetSelector.addGoal(0, new HurtByTargetGoal(this, Sheep.class));
            this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Creeper.class, true));
        }
    }*/

    public class PathfinderTest extends CustomPathfinder {

        private Entity target;

        protected PathfinderTest(@NotNull Mob m) {
            super(m);
            m.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
        }

        @Override
        public @NotNull PathfinderFlag[] getFlags() {
            return new PathfinderFlag[0];
        }

        @Override
        public boolean canStart() {
            boolean canStart = false;
            for (Entity nearbyEntity : entity.getNearbyEntities(5, 5, 5)) {
                if (nearbyEntity.getType() == EntityType.PLAYER ||
                        nearbyEntity.getType() == EntityType.ZOMBIE) {
                    canStart = true;
                    target = nearbyEntity;
                }
            }
            return canStart;
        }

        @Override
        public void start() {
            Bukkit.broadcastMessage("GET AWAY FROM ME");
            entity.attack(target);
        }

        @Override
        public void tick() {

        }
    }
}
