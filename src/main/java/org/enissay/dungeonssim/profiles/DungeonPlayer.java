package org.enissay.dungeonssim.profiles;

import java.util.Date;

public class DungeonPlayer {

    private Rank rank;
    private String id, name;
    private Date joinDate, lastOnline;
    private int coins;

    public DungeonPlayer(String id, String name, Rank rank, Date joinDate, Date lastOnline, int coins) {
        this.rank = rank;
        this.id = id;
        this.name = name;
        this.joinDate = joinDate;
        this.lastOnline = lastOnline;
        this.coins = coins;
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

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}