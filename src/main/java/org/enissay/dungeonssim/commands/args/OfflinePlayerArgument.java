package org.enissay.dungeonssim.commands.args;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.message.MessageRegistry;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class OfflinePlayerArgument extends ArgumentResolver<CommandSender, OfflinePlayer> {

    private final Server server;

    public OfflinePlayerArgument(Server server) {
        this.server = server;
    }

    @Override
    protected ParseResult<OfflinePlayer> parse(Invocation<CommandSender> invocation, Argument<OfflinePlayer> context, String argument) {
        OfflinePlayer player = server.getOfflinePlayer(argument);

        if (player != null) {
            return ParseResult.success(player);
        }

        return ParseResult.failure("Player " + argument + " not found! (PLAYER_NOT_FOUND)");
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<OfflinePlayer> argument, SuggestionContext context) {
        return Arrays.stream(server.getOfflinePlayers()).toList().stream()
                .map(OfflinePlayer::getName)
                .collect(SuggestionResult.collector());
    }

}