package org.enissay.dungeonssim.commands.args;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.enissay.dungeonssim.handlers.ProfilesHandler;
import org.enissay.dungeonssim.profiles.Rank;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RankArgument extends ArgumentResolver<CommandSender, Rank> {

    @Override
    protected ParseResult<Rank> parse(Invocation<CommandSender> invocation, Argument<Rank> context, String argument) {
        Rank rank = Rank.valueOf(argument);
        if (rank != null) {
            return ParseResult.success(rank);
        }

        return ParseResult.failure(ChatColor.RED + "Rank " + argument + " not found! (RANK_NOT_FOUND)");
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<Rank> argument, SuggestionContext context) {
        return Arrays.stream(Rank.values()).map(Rank::toString).collect(SuggestionResult.collector());
    }
}
