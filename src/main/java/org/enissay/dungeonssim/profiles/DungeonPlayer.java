package org.enissay.dungeonssim.profiles;

import org.bukkit.ChatColor;
import org.enissay.dungeonssim.utils.LevelUtil;

import java.util.Date;
import java.util.Objects;

public class DungeonPlayer {

    private static double BASE = 1.316;

    private Rank rank;
    private String id, name;
    private Date joinDate, lastOnline;
    private int coins;
    private PlayerClass playerClass;
    private long exp;

    public DungeonPlayer(String id, String name, Rank rank, Date joinDate, Date lastOnline, int coins) {
        this.rank = rank;
        this.id = id;
        this.name = name;
        this.joinDate = joinDate;
        this.lastOnline = lastOnline;
        this.coins = coins;
        this.playerClass = PlayerClass.NONE;
        this.exp = 0L;
    }

    public PlayerClass getPlayerClass() {
        return playerClass != null ? playerClass : PlayerClass.NONE;
    }

    public long getExp() {
        return !Objects.isNull(this.exp) ? exp : 0;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getID() {
        return id;
    }

    public Rank getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public int getCoins() {
        return coins;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getLevel() {
        if (exp < 0) {
            return 1;
        }

        int level = 1;
        while (exp >= LevelUtil.maxExpForLevel(level)) {
            level++;
        }
        return level;
    }

    public long getMaxEXP() {
        int level = getLevel();
        return LevelUtil.maxExpForLevel(level);
    }
}