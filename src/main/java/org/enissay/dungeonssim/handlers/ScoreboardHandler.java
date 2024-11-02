package org.enissay.dungeonssim.handlers;

import com.google.common.collect.Lists;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.enissay.dungeonssim.DungeonsSim;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.system.Dungeon;
import org.enissay.dungeonssim.profiles.DungeonPlayer;
import org.enissay.dungeonssim.profiles.Rank;
import org.enissay.dungeonssim.utils.FormatUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHandler {
    public static Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
    public static Objective objective;

    public static void register() {
        objective = sb.registerNewObjective("test", "dummy");

        Arrays.stream(Rank.values()).forEach(rank -> {
            final String teamName = (Rank.values().length - rank.getPower()) + rank.name();
            sb.registerNewTeam(teamName);
            sb.getTeam(teamName).setPrefix(rank.getColor().toString() + rank.name() + " ");
            //sb.getTeam(teamName).setDisplayName(rank.getColor().toString());
        });
    }

    public static void init() {
        register();
        update();
    }

    public static void render(final Player player, final BPlayerBoard board) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(DungeonsSim.getInstance(), new Runnable() {
            public void run() {
                final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
                if (dungeonPlayer != null) {
                    final Dungeon dungeon = DungeonHandler.getDungeonOf(player.getUniqueId());
                    final DungeonParty party = PartyHandler.getPartyOf(player.getUniqueId());
                    final Rank rank = dungeonPlayer.getRank();
                    final int coins = dungeonPlayer.getCoins();

                    final List<String> def = new ArrayList<>(List.of(ChatColor.DARK_GRAY + " - " + rank.getColor().toString() + player.getName(),
                            ChatColor.GRAY.toString(),
                            ChatColor.DARK_GRAY + "• " + ChatColor.RESET + "Rank: " + rank.getColor() + rank.name(),
                            //ChatColor.DARK_GRAY.toString(),
                            ChatColor.DARK_GRAY + "• " + ChatColor.RESET + "Coins: " + ChatColor.GREEN + FormatUtil.format(coins),
                            ChatColor.GREEN.toString()));
                    if (party != null) {
                        def.add(ChatColor.DARK_GRAY + "• " + ChatColor.RESET + "Party: " + ChatColor.GOLD + party.getName() + ChatColor.GRAY + " (" +
                                ChatColor.LIGHT_PURPLE + party.getPlayers().size() + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_PURPLE + party.getMaxPlayers() + ChatColor.GRAY + ")");
                        def.add(ChatColor.WHITE + "");
                    }
                    if (dungeon != null) {
                        int puzzles = dungeon.getRooms().stream()
                                .filter(r -> r.getTemplate().getName().contains("PUZZLE"))
                                .collect(Collectors.toList()).size();

                        def.add(ChatColor.DARK_GRAY + "• " + ChatColor.RESET + "Dungeon: " + ChatColor.AQUA + "#" + ChatColor.BOLD + dungeon.getID() + ChatColor.DARK_GRAY + " - " + ChatColor.BLUE + dungeon.getProgression() + "%");
                        def.add("  " + ChatColor.GRAY + "→" + ChatColor.RESET + " Time: " + ChatColor.GREEN + dungeon.getTime());
                        def.add("  " + ChatColor.GRAY + "→" + ChatColor.RESET + " Rooms opened: " + ChatColor.LIGHT_PURPLE + dungeon.getRoomsOpened().size() + ChatColor.GRAY + "/" + ChatColor.DARK_PURPLE + dungeon.getDungeonGeneration().getKeyRooms().size());
                        def.add("  " + ChatColor.GRAY + "→" + ChatColor.RESET + " Players: " + ChatColor.DARK_AQUA + dungeon.getPlayers().size());
                        def.add("  " + ChatColor.GRAY + "→" + ChatColor.RESET + " Difficulty: " + ChatColor.GREEN + dungeon.getDungeonDifficulty().name());
                        def.add("  " + ChatColor.GRAY + "→" + ChatColor.RESET + " Mobs: " + ChatColor.RED + dungeon.getAliveMobs().size() + ChatColor.GRAY + "/" + ChatColor.RED + dungeon.getMobs().size());
                        def.add("  " + ChatColor.GRAY + "→" + ChatColor.RESET + " Key: " + (dungeon.hasBossKey() ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖"));
                        def.add("  " + ChatColor.GRAY + "→" + ChatColor.RESET + " Puzzles: " + ChatColor.GRAY + "[" + ChatColor.AQUA + dungeon.getCompletedPuzzles().size() + ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + puzzles + ChatColor.GRAY + "]");
                        def.add(ChatColor.BLUE + "");//✔ & ✖
                    }

                    String[] stringArray = def.toArray(new String[0]);

                    board.setAll(stringArray);
                    board.setName(ChatColor.GREEN.toString() + ChatColor.BOLD + "DUNGEONSSIM" + ChatColor.GRAY + " | " + ChatColor.GOLD + ChatColor.BOLD + "BETA" + ChatColor.GRAY + " - " + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size());
                }
            }
        }, 0, 20);
    }

    public static void update() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final DungeonPlayer dungeonPlayer = ProfilesHandler.findProfile(player.getUniqueId().toString());
            if (dungeonPlayer != null) {
                sb.getTeam((Rank.values().length - dungeonPlayer.getRank().getPower()) + dungeonPlayer.getRank().name()).addPlayer((OfflinePlayer)player);
                //player.setDisplayName(sb.getTeam(minefortPlayer.getRank().getPower() + minefortPlayer.getRank().name()).getPrefix() + player.getName());
            }
            player.setScoreboard(sb);
        });
    }

    public static Scoreboard getScoreboard() {
        return sb;
    }
}