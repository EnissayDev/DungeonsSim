package org.enissay.dungeonssim.commands.args;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.enissay.dungeonssim.dungeon.party.DungeonParty;
import org.enissay.dungeonssim.dungeon.party.DungeonRole;
import org.enissay.dungeonssim.dungeon.party.PartyMember;
import org.enissay.dungeonssim.handlers.PartyHandler;

import java.util.ArrayList;
import java.util.List;

public class PartyMemberArgument extends ArgumentResolver<CommandSender, PartyMember> {

    private final Server server;

    public PartyMemberArgument(Server server) {
        this.server = server;
    }

    @Override
    protected ParseResult<PartyMember> parse(Invocation<CommandSender> invocation, Argument<PartyMember> context, String argument) {
        //OfflinePlayer player = server.getOfflinePlayer(argument);
        DungeonParty party = PartyHandler.getPartyOf(Bukkit.getOfflinePlayer(argument).getUniqueId());
        if (party != null) {
            return ParseResult.success(new PartyMember(Bukkit.getOfflinePlayer(argument).getUniqueId(), party));
        }

        return ParseResult.failure(ChatColor.RED + "Party member " + argument + " not found! (PARTY_MEMBER_NOT_FOUND)");
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<PartyMember> argument, SuggestionContext context) {
        final Player player = (Player) invocation.sender();
        final List<String> members = new ArrayList<>();
        DungeonParty party = PartyHandler.getPartyOf(player.getUniqueId());
        if (party != null) {
            party.getPlayers(DungeonRole.PLAYER).forEach(dp -> {
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(dp);
                members.add(offlinePlayer.getName());
            });
        }
        return members.stream().collect(SuggestionResult.collector());
        /*return dungeon != null && dungeon.getPlayers().size() > 1 ? dungeon.getPlayers().stream()
                //.filter(uuid -> uuid.compareTo(((Player) invocation.sender()).getUniqueId()) != 0)
                .map(dgplayer -> {
                    return Bukkit.getOfflinePlayer(dgplayer).getName();
                })
                .collect(SuggestionResult.collector()) : Arrays.asList(new String[]{"none"}).stream().collect(SuggestionResult.collector());*/
    }
}
