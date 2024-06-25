package org.enissay.dungeonssim.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;

import java.util.List;
import java.util.stream.Collectors;

public class MessageUtils {
    public enum BroadcastType {
        TITLE,
        SUB_TITLE,
        ACTION_BAR,
        MESSAGE;
    }

    public enum TargetType {
        DUNGEON,
        PARTY,
        NORMAL;
    }

    public static void broadcastParty(DungeonParty party, String msg, BroadcastType type) {
        broadcast(party.getPlayers().keySet().stream().map(player -> {
            return Bukkit.getPlayer(player);
        }).collect(Collectors.toList()), msg, type);
    }

    public static void broadcastDungeon(Dungeon dungeon, String msg, BroadcastType type) {
        broadcast(dungeon.getPlayers().stream().map(player -> {
            return Bukkit.getPlayer(player);
        }).collect(Collectors.toList()), msg, type);
    }

    public static void broadcastPartySound(DungeonParty party, Sound sound, float volume, float pitch) {
        broadcastSound(party.getPlayers().keySet().stream().map(player -> {
            return Bukkit.getPlayer(player);
        }).collect(Collectors.toList()), sound, volume, pitch);
    }

    public static void broadcastDungeonSound(Dungeon dungeon, Sound sound, float volume, float pitch) {
        broadcastSound(dungeon.getPlayers().stream().map(player -> {
            return Bukkit.getPlayer(player);
        }).collect(Collectors.toList()), sound, volume, pitch);
    }

    public static void broadcastSound(Sound sound, float volume, float pitch, TargetType targetType, Object obj) /*throws Exception */{
        switch (targetType) {
            case DUNGEON:
                if (obj instanceof Dungeon) {
                    broadcastDungeonSound((Dungeon)obj, sound, volume, pitch);
                }//else throw new Exception("Object isn't of type Dungeon");
                break;
            case PARTY:
                if (obj instanceof DungeonParty) {
                    broadcastPartySound((DungeonParty) obj, sound, volume, pitch);
                }//else throw new Exception("Object isn't of type Dungeon");
                break;
            case NORMAL:
                if (obj instanceof List) {
                    final List<Player> players = (List<Player>) obj;
                    broadcastSound(players, sound, volume, pitch);
                }
                break;
        }
    }
    public static void broadcast(String msg, BroadcastType type, TargetType targetType, Object obj) /*throws Exception */{
        msg = msg.replace("&", "§");
        switch (targetType) {
            case DUNGEON:
                if (obj instanceof Dungeon) {
                    broadcastDungeon((Dungeon)obj, msg, type);
                }//else throw new Exception("Object isn't of type Dungeon");
                break;
            case PARTY:
                if (obj instanceof DungeonParty) {
                    broadcastParty((DungeonParty)obj, msg, type);
                }//else throw new Exception("Object isn't of type Dungeon");
                break;
            case NORMAL:
                if (obj instanceof List) {
                    final List<Player> players = (List<Player>) obj;
                    broadcast(players, msg, type);
                }
                break;
        }
    }

    public static void broadcastSound(List<Player> players, Sound sound, float volume, float pitch) {
        if (players.size() > 0) {
            players.forEach(player -> {
                player.playSound(player.getLocation(), sound, volume, pitch);
            });
        }
    }
    public static void broadcast(List<Player> players, String msg, BroadcastType type) {
        msg = msg.replace("&", "§");
        if (players.size() > 0) {
            String finalMsg = msg;
            players.forEach(player -> {
                switch (type) {
                    case MESSAGE:
                        player.sendMessage(finalMsg);
                        break;
                    case ACTION_BAR:
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(finalMsg));
                        break;
                    case TITLE:
                        player.sendTitle(finalMsg, "", 0, 30, 10);
                        break;
                    case SUB_TITLE:
                        player.sendTitle("", finalMsg, 0, 30, 10);
                        break;

                }
            });
        }
    }
}
