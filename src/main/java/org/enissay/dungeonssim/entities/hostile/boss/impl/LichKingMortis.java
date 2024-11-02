package org.enissay.dungeonssim.entities.hostile.boss.impl;

import eu.decentsoftware.holograms.api.utils.scheduler.S;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.EventManager;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.dungeon.system.DungeonRoom;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.hostile.boss.AbstractBossMob;
import org.enissay.dungeonssim.entities.hostile.boss.BossPhase;
import org.enissay.dungeonssim.entities.hostile.boss.impl.abilities.FrostNovaAbility;
import org.enissay.dungeonssim.entities.hostile.boss.impl.abilities.LaserBeamAbility;
import org.enissay.dungeonssim.entities.hostile.boss.impl.abilities.SummonUndeadMinionsAbility;
import org.enissay.dungeonssim.entities.hostile.skeleton.CustomArrow;
import org.enissay.dungeonssim.items.ItemsList;
import org.enissay.dungeonssim.utils.FormatUtil;
import org.joml.Vector3d;

import java.util.*;

public class LichKingMortis extends AbstractBossMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "LichKingMortis");

    private Location spawnLocation;
    private Dungeon dungeon;
    //private Map<Dungeon, Block> map;
    private Block beacon;
    private Map<Block, Float> blockHeart;
    private Map<Block, BlockData> beaconData;
    private List<AbstractCustomMob> additionalMobs;
    TextDisplay beaconName;
    private static Sound BOSS_SOUND = Sound.ENTITY_ELDER_GUARDIAN_HURT;
    private float maxBeaconHealth = (float) (getHealth() * 1.3);
    public LichKingMortis(Location loc, int mobLevel, double healthMultiplier, Dungeon dungeon) {
        super(EntityType.SKELETON, loc, KEY, mobLevel, healthMultiplier); // Example phase threshold of 25% health
        this.spawnLocation = loc;
        this.dungeon = dungeon;
        this.blockHeart = new HashMap<>();
        this.additionalMobs = new ArrayList<>();

        final ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        final LeatherArmorMeta meta = (LeatherArmorMeta) chest.getItemMeta();
        meta.setColor(Color.fromARGB(1, 3, 157, 252));
        chest.setItemMeta(meta);
        final ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        final LeatherArmorMeta meta2 = (LeatherArmorMeta) leg.getItemMeta();
        meta2.setColor(Color.fromARGB(1, 3, 157, 252));
        leg.setItemMeta(meta2);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        final LeatherArmorMeta meta3 = (LeatherArmorMeta) boots.getItemMeta();
        meta3.setColor(Color.fromARGB(1, 3, 157, 252));
        boots.setItemMeta(meta3);

        equipItem(EquipmentSlot.CHEST, chest);
        equipItem(EquipmentSlot.LEGS, leg);
        equipItem(EquipmentSlot.FEET, boots);

        applyCustomHead();

        createPhases().forEach(phase -> addPhase(phase));
        EventManager.on(PlayerInteractEvent.class, event -> {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK){
                final Block block = event.getClickedBlock();
                if (dungeon != null && this.isAlive() &&
                        blockHeart.get(block) != null && blockHeart.containsKey(block) &&
                        beacon != null && event.getClickedBlock().equals(beacon)) {
                    final Player damager = event.getPlayer();
                    final ItemStack itemInHand = damager.getItemInHand();
                    final ItemsList item = ItemsList.getItemByID(NBTEditor.getInt(itemInHand, "ID"));

                    if (item != null && NBTEditor.getString(itemInHand, "attack_damage") != null) {
                        double damage = Double.parseDouble(NBTEditor.getString(itemInHand, "attack_damage"));
                        final float remaining = (float) (blockHeart.get(block) - damage);
                        blockHeart.put(block, remaining >= 0 ? remaining : 0);
                        String[] blockBrokenDialogues = {
                                "&fNo! You dare destroy my source of power? I will make you pay for this insolence!",
                                "&fThe Heart of Regeneration... shattered! This changes nothing. Your end is still nigh!"
                        };
                        if (remaining <= 0) {
                            if (beaconData != null)
                                revertBlocks(beaconData);
                            block.setType(Material.AIR);
                            beaconName.remove();
                            sendDialoguesWithDelay(getTargets(), blockBrokenDialogues, 40, BOSS_SOUND, 1.5f, 2f);
                        }
                        else if (beaconName != null){
                            beaconName.setText(ChatColor.DARK_GREEN + "[Lvl. " + LichKingMortis.this.getMobLevel() + "] " + LichKingMortis.this.getColor() + "Heart of Regeneration" + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(blockHeart.get(beacon)) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(maxBeaconHealth) + "❤");
                        }
                        loc.getWorld().playSound(block.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1, 1);
                    }
                }
            }
        });
    }

    @Override
    public void die(DamageSource damagesource) {
        additionalMobs.forEach(ad -> ad.remove(RemovalReason.KILLED));
        super.die(damagesource);
    }

    private List<LivingEntity> getTargets() {
        List<LivingEntity> targets = new ArrayList<>();
        dungeon.getPlayers().forEach(uuid ->
                targets.add(getLivingEntityFromPlayer(Bukkit.getPlayer(uuid))));
        return targets;
    }

    private Map<Block, BlockData> createHexagonAnimation(Location center) {
        World world = center.getWorld();
        int[][] offsets = {{0, 0},
                {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1},
                {2, 0}, {0, 2}, {-2, 0}, {0, -2}
        };

        Map<Block, BlockData> originalBlocks = new HashMap<>();

        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (step >= offsets.length) {
                    cancel();
                    //revertBlocks(originalBlocks);
                    return;
                }

                int[] offset = offsets[step];
                Block block = world.getBlockAt(center.clone().add(offset[0], -1, offset[1]));
                originalBlocks.put(block, block.getBlockData());
                block.setType(Material.OBSIDIAN);
                block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_NETHER_BRICKS_BREAK, 1, .1f);

                step++;
            }
        }.runTaskTimer(DungeonsSim.getInstance(), 0, 1); // Adjust the delay between steps as needed
        return originalBlocks;
    }

    private void revertBlocks(Map<Block, BlockData> originalBlocks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<Block, BlockData> entry : originalBlocks.entrySet()) {
                    entry.getKey().setBlockData(entry.getValue());
                }
            }
        }.runTaskLater(DungeonsSim.getInstance(), 20); // Adjust the delay for how long the blocks stay as obsidian
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public LivingEntity getLivingEntityFromPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return (LivingEntity) craftPlayer.getHandle();
    }

    public Goal walkToLocation(Location targetLocation, double speed) {
        Vector3d vec3d = new Vector3d(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
        Goal goal = new PathfinderGoalWalkToLocation(this, vec3d, speed);
        this.goalSelector.addGoal(1, goal);
        return goal;
    }

    public List<BossPhase> createPhases() {
        List<BossPhase> phases = new ArrayList<>();

        String[] phase1Dialogues = new String[]{"&fTremble before the might of Lich King Mortis! Your souls will be mine!",
        "&fFeel the darkness consume you!",
        "&fYou are but mere insects in the presence of my might. Prepare to be crushed!"};

        String[] phase2Dialogues = new String[]{"&c&n&oImpressive&f... &fbut you have only witnessed a fraction of my &ctrue power&f!",
                "&fThe shadows grow deeper, and your fate grows bleaker!",
                "&fYou think you can defeat me? The real battle has only just begun!"};

        String[] phase3Dialogues = new String[]{"&fNo! This cannot be! I am the eternal ruler of the undead!",
                "&fFeel the wrath of a thousand souls! This ends now!",
                "&4You may have pushed me to my limits, but I will not fall alone. Prepare to meet your doom!"};

        String[] blockFullyHealedDialogues = {
                "&fFoolish mortals! My strength is restored, and your doom is inevitable!",
                "&fThe Heart of Regeneration has revived me! Your efforts are in vain. I am unstoppable!"
        };
        if (dungeon != null) {
            List<LivingEntity> targets = new ArrayList<>();
            dungeon.getPlayers().forEach(uuid ->
                targets.add(getLivingEntityFromPlayer(Bukkit.getPlayer(uuid))));
            phases.add(new BossPhase(1) {
                @Override
                protected void onStartPhase(AbstractBossMob boss) {
                    int finishImmunity = 40 * phase2Dialogues.length;
                    final DungeonRoom dungeonRoom = dungeon.getRooms().stream().filter(r -> r.getTemplate().getName().contains("BOSS")).findFirst().orElse(null);
                    final Location loc1 = dungeonRoom.getLocationFromTemplate("floorLoc1");
                    final Location loc2 = dungeonRoom.getLocationFromTemplate("floorLoc2");
                    if (loc1 != null && loc2 != null)
                        addAbility(new LaserBeamAbility(20 * 1000, 20, 10 * 20, new Location[]{loc1, loc2}));
                    setImmune(true);
                    boss.setNoGravity(true);
                    boss.setNoAi(true);

                    sendDialoguesWithDelay(targets, phase1Dialogues, 40, BOSS_SOUND, 1.5f, 2f);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            boss.setNoGravity(false);
                            boss.setNoAi(false);

                            setImmune(false);
                        }
                    }.runTaskLater(DungeonsSim.getInstance(), finishImmunity);
                    //sendDialoguesWithDelay(targets, phase1Dialogues, 40, Sound.ENTITY_ILLUSIONER_HURT, .1f, .7f);
                    //sendDialogue(targets, "Tremble before the might of Lich King Mortis! Your souls will be mine!");
                }
            });

            phases.add(new BossPhase(0.5) {
                @Override
                protected void onStartPhase(AbstractBossMob boss) {
                    int finishImmunity = 40 * phase2Dialogues.length;

                    Goal moveGoal = ((LichKingMortis) boss).walkToLocation(spawnLocation, 1.5);

                    clearAbilities();
                    setImmune(true);
                    //boss.setNoGravity(true);
                    //boss.setNoAi(true);

                    sendDialoguesWithDelay(targets, phase2Dialogues, 40, BOSS_SOUND, 1.5f, 2f);
                    if (dungeon != null) {
                        SummonUndeadMinionsAbility summon = new SummonUndeadMinionsAbility(1000 * 20, 5, 1);
                        addAbility(summon);
                        summon.getMinions().forEach(m -> LichKingMortis.this.additionalMobs.add(m));

                        final Location location = dungeon.getRooms().stream().filter(b -> b.getTemplate().getName().contains("BOSS"))
                                .findFirst().orElse(null)
                                .getLocationFromTemplate("regenBlock");
                        Block beacon = location.getBlock();
                        beacon.setType(Material.BEACON);
                        LichKingMortis.this.beacon = beacon;
                        blockHeart.put(beacon, maxBeaconHealth);

                        beaconName = (TextDisplay) location.getWorld().spawnEntity(
                                location.clone().add(0, 1, 0), org.bukkit.entity.EntityType.TEXT_DISPLAY);
                        beaconName.setText(ChatColor.DARK_GREEN + "[Lvl. " + LichKingMortis.this.getMobLevel() + "] " + LichKingMortis.this.getColor() + "Heart of Regeneration" + ChatColor.DARK_GRAY + " → " + ChatColor.RED + FormatUtil.format(blockHeart.get(beacon)) + ChatColor.GRAY + "/" + ChatColor.RED + FormatUtil.format(maxBeaconHealth) + "❤");
                        // Apply a glowing effect for critical hits
                        beaconName.setCustomNameVisible(false);
                        beaconName.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
                        beaconName.setPersistent(false);
                        beaconData = createHexagonAnimation(beacon.getLocation());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (getHealth() >= getMaxHealth()) {
                                    if (beaconData != null)
                                        revertBlocks(beaconData);
                                    beacon.setType(Material.AIR);
                                    blockHeart.put(beacon, 0f);
                                    beaconName.remove();
                                    sendDialoguesWithDelay(getTargets(), blockFullyHealedDialogues, 40, BOSS_SOUND, 1.5f, 2f);
                                }
                                if (blockHeart.get(beacon) == 0) {
                                    LichKingMortis.this.goalSelector.removeGoal(moveGoal);
                                    LichKingMortis.this.goalSelector.addGoal(1, getMeleeGoal());
                                    setImmune(false);
                                    cancel();
                                }else {
                                    final float amountToRegen = (float) (LichKingMortis.this.getHealth()*0.025);
                                    if ((LichKingMortis.this.getHealth() + amountToRegen) <= LichKingMortis.this.getMaxHealth())
                                        LichKingMortis.this.setHealth(LichKingMortis.this.getHealth() + amountToRegen);
                                    else LichKingMortis.this.setHealth(LichKingMortis.this.getMaxHealth());

                                    Location start = beacon.getLocation().add(0.5, 0.5, 0);
                                    Location end = LichKingMortis.this.getBukkitEntity().getLocation().add(0, 1, 0);
                                    Vector dir = end.toVector().subtract(start.toVector()).normalize();
                                    double distance = start.distance(end);
                                    double step = 0.1;

                                    for (double d = 0; d < distance; d += step) {
                                        Location point = start.clone().add(dir.clone().multiply(d));
                                        point.getWorld().spawnParticle(
                                                Particle.REDSTONE,
                                                point,
                                                1,
                                                new Particle.DustOptions(Color.AQUA, 1)
                                        );
                                    }
                                    updateName();
                                }
                            }
                        }.runTaskTimer(DungeonsSim.getInstance(), 0, 20);
                    }else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                LichKingMortis.this.goalSelector.removeGoal(moveGoal);
                                LichKingMortis.this.goalSelector.addGoal(1, getMeleeGoal());
                                setImmune(false);
                            }
                        }.runTaskLater(DungeonsSim.getInstance(), finishImmunity);
                    }
                    /*new BukkitRunnable() {
                        @Override
                        public void run() {
                            LichKingMortis.this.goalSelector.removeGoal(moveGoal);
                            LichKingMortis.this.goalSelector.addGoal(1, getMeleeGoal());
                            setImmune(false);
                        }
                    }.runTaskLater(DungeonsSim.getInstance(), finishImmunity);*/
                }
            });

            // Phase 3: Starts at 25% health
            phases.add(new BossPhase(0.25) {
                int finishImmunity = 40 * phase3Dialogues.length;

                @Override
                protected void onStartPhase(AbstractBossMob boss) {
                    sendDialoguesWithDelay(targets, phase3Dialogues, 40, BOSS_SOUND, 1.5f, 2f);
                    setImmune(true);
                    clearAbilities();
                    Location initialLocation = boss.getBukkitEntity().getLocation(); // Store initial location

                    new BukkitRunnable() {
                        int ticks = 0;

                        @Override
                        public void run() {
                            if (ticks >= 100) { // 5 seconds * 20 ticks per second = 100 ticks
                                // Stop spinning and shooting arrows
                                getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.55);
                                getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 1.4);
                                setImmune(false);
                                cancel();
                                return;
                            }

                            // Spin the mob
                            initialLocation.setYaw(initialLocation.getYaw() + 36); // Adjust the yaw for spinning effect
                            boss.getBukkitEntity().teleport(initialLocation); // Teleport back to initial location

                            // Shoot arrows randomly
                            if (ticks % 2 == 0) { // Shoot an arrow every 5 ticks (0.25 seconds)
                                for (int i = 0; i < 4; i++)
                                    shootRandomArrow(boss);
                            }
                            ticks++;
                        }
                    }.runTaskTimer(DungeonsSim.getInstance(), 0, 1); // Run every tick
                }
            });
        }

        return phases;
    }

    private void shootRandomArrow(AbstractBossMob boss) {
        World world = boss.getBukkitEntity().getWorld();
        Location spawnLocation = boss.getBukkitEntity().getLocation();

        // Calculate a random direction to shoot the arrow
        /*double randomPitch = Math.random() * 360 - 180;
        double randomYaw = Math.random() * 360 - 180; // Random yaw between -180 and 180 degrees
        */
        final double angle = 30;
        double randomPitch = Math.random() * angle - (angle/2); // Random pitch between -180 and 180 degrees
        double randomYaw = Math.random() * 360 - 180 /*- (angle/2)*/; // Random yaw between -180 and 180 degrees

        Vector direction = new Vector(
                Math.cos(Math.toRadians(randomPitch)) * Math.cos(Math.toRadians(randomYaw)),
                Math.sin(Math.toRadians(randomPitch)),
                Math.cos(Math.toRadians(randomPitch)) * Math.sin(Math.toRadians(randomYaw))
        );

        double attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 1.5;
        CustomArrow arrow = new CustomArrow((Level) this.level, spawnLocation.getX(), spawnLocation.getY() + this.getEyeHeight(), spawnLocation.getZ(), (int) attackDamage);
        arrow.setOwner(this);
        arrow.shoot(direction.getX(), direction.getY(), direction.getZ(), 2.0F, 1.0F); // Shoot with 2.0 velocity and 1.0 accuracy
        this.level.addFreshEntity(arrow);

        world.playSound(this.getBukkitEntity(), Sound.ENTITY_SKELETON_SHOOT, 1.0f, 1.5f);

        this.swing(InteractionHand.MAIN_HAND);

        /*Arrow arrow = (Arrow) world.spawnEntity(spawnLocation.add(0, 1.5, 0), org.bukkit.entity.EntityType.ARROW);
        //arrow.setShooter(boss.getBukkitEntity());
        arrow.setVelocity(direction.normalize().multiply(1.5)); // Adjust speed if necessary*/
    }

    @Override
    public Map<Attribute, Double> getCustomAttributes() {
        Map<Attribute, Double> attrs = new HashMap<>();
        //attrs.put(Attributes.ATTACK_DAMAGE, 5.0);
        attrs.put(Attributes.KNOCKBACK_RESISTANCE, 10000.0);
        //attrs.put(Attributes.ATTACK_SPEED, .25);
        attrs.put(Attributes.FOLLOW_RANGE, 35.0);
        attrs.put(Attributes.MOVEMENT_SPEED, .45);
        //attrs.put(Attributes.MAX_HEALTH, 100D);
        //attrs.put(Attributes.MOVEMENT_SPEED, .35);
        return attrs;
    }

    @Override
    public String getEntityCustomName() {
        return "Lich King Mortis";
    }

    @Override
    public String[] getCustomHeads() {
        return new String[]{"e0dc0074d13c43df185d3e6e66c44b2c71e01a1f907505a8e6960f2ce08c4cc8"};
    }

    @Override
    public Set<ItemStack> getDrops() {
        return null;
    }
    public class PathfinderGoalWalkToLocation extends Goal {
        private final Mob mob;
        private final double speed;
        private final Vector3d targetLocation;

        public PathfinderGoalWalkToLocation(Mob mob, Vector3d targetLocation, double speed) {
            this.mob = mob;
            this.targetLocation = targetLocation;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            return true; // Always run this goal
        }

        @Override
        public void tick() {
            this.mob.getNavigation().moveTo(targetLocation.x, targetLocation.y, targetLocation.z, speed);
        }

        @Override
        public boolean canContinueToUse() {
            // Continue until the mob reaches the target location
            return !this.mob.getNavigation().isDone();
        }
    }
}