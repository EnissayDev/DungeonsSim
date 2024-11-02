package org.enissay.dungeonssim.entities.hostile.boss;

import me.lucko.helper.bossbar.BukkitBossBarFactory;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.entities.AbstractCustomMob;
import org.enissay.dungeonssim.entities.hostile.AbstractHostileCustomMob;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBossMob extends AbstractHostileCustomMob {

    public static final NamespacedKey KEY = new NamespacedKey(DungeonsSim.getInstance(), "Boss");
    private int currentPhaseIndex;
    private org.bukkit.boss.BossBar bossBar;
    private List<BossPhase> phases;

    protected AbstractBossMob(EntityType<? extends PathfinderMob> entityType, Location spawnLocation, NamespacedKey key, int mobLevel, double healthMultiplier, List<BossPhase> phases) {
        super(entityType, spawnLocation, key, mobLevel, healthMultiplier);

        this.currentPhaseIndex = -1;
        this.phases = phases;
        //this.players = new ArrayList<>();

        if (!phases.isEmpty() && phases.get(0).getThresholdHealth(getMaxHealth()) == 1) {
            phases.get(0).startPhase(this);
        }

        bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        //bossBar = BossBar.bossBar(Component.text(this.getBukkitEntity().getCustomName()), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        //bossBar.addFlags(BossBar.Flag.PLAY_BOSS_MUSIC, BossBar.Flag.DARKEN_SCREEN, BossBar.Flag.CREATE_WORLD_FOG);

    }

    protected AbstractBossMob(EntityType<? extends PathfinderMob> entityType, Location spawnLocation, NamespacedKey key, int mobLevel, double healthMultiplier) {
        super(entityType, spawnLocation, key, mobLevel, healthMultiplier);

        this.currentPhaseIndex = -1;
        this.phases = new ArrayList<>();
        //this.players = new ArrayList<>();

        bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        //bossBar = BossBar.bossBar(Component.text(this.getBukkitEntity().getCustomName()), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        //bossBar.addFlags(BossBar.Flag.PLAY_BOSS_MUSIC, BossBar.Flag.DARKEN_SCREEN, BossBar.Flag.CREATE_WORLD_FOG);

    }

    public List<BossPhase> getPhases() {
        return phases;
    }

    public int getCurrentPhaseIndex() {
        return currentPhaseIndex;
    }

    public void addPhase(BossPhase bossPhase) {
        this.phases.add(bossPhase);
        if (phases.get(0).getThresholdHealth(getMaxHealth()) == 1) {
            phases.get(0).startPhase(this);
        }
    }

    @Override
    protected void tickDeath() {
        bossBar.getPlayers().forEach(pl -> bossBar.removePlayer(pl));
        bossBar.removeAll();
        /*players.forEach(pl -> {
            DungeonsSim.getInstance().adventure().player(pl).hideBossBar(bossBar);
            bossBar.removeViewer(DungeonsSim.getInstance().adventure().player(pl));
        });*/

        super.tickDeath();
    }

    @Override
    public void tick() {
        if (getHealth() > 0) {
            bossBar.setProgress(getHealth() / getMaxHealth());
            bossBar.setTitle(this.getBukkitEntity().getCustomName());
            //bossBar.name(Component.text(this.getBukkitEntity().getCustomName()));

            getBukkitEntity().getWorld().getNearbyEntities(getBukkitEntity().getLocation(), 15, 15, 15).forEach(pl -> {
                if (pl instanceof Player player && (!bossBar.getPlayers().contains(player))) {
                    bossBar.addPlayer(player);
                }
            });
        }
        else {
            bossBar.getPlayers().forEach(pl -> bossBar.removePlayer(pl));
            bossBar.removeAll();
        }
        /*if (getHealth() > 0) {
            bossBar.progress(getHealth() / getMaxHealth());
            //bossBar.name(Component.text(this.getBukkitEntity().getCustomName()));

            getBukkitEntity().getWorld().getNearbyEntities(getBukkitEntity().getLocation(), 15, 15, 15).forEach(pl -> {
                if (pl instanceof Player player && (!players.contains(player))) {
                    DungeonsSim.getInstance().adventure().player(player).showBossBar(bossBar);
                    players.add(player);
                }
            });
        }
        else {
            players.forEach(pl -> {
                DungeonsSim.getInstance().adventure().player(pl).hideBossBar(bossBar);
                bossBar.removeViewer(DungeonsSim.getInstance().adventure().player(pl));
            });
        }*/
        if (this.isAlive()) {
            this.phases.forEach(phase -> {
                if (getHealth() <= phase.getThresholdHealth(getMaxHealth()) &&
                        phases.indexOf(phase) <= (phases.size() - 1) &&
                        this.currentPhaseIndex < phases.indexOf(phase)) {
                    phase.startPhase(this);
                    this.currentPhaseIndex=phases.indexOf(phase);
                }
            });
        }
        super.tick();
    }
    private List<LivingEntity> getNearbyEntities(double range) {
        return getBukkitEntity().getWorld().getNearbyEntities(getBukkitEntity().getLocation(), range, range, range)
                .stream()
                .filter(entity -> entity instanceof LivingEntity && entity != this)
                .map(entity -> (LivingEntity) entity)
                .collect(Collectors.toList());
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public boolean isBoss() {
        return true;
    }
}