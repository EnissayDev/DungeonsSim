package org.enissay.dungeonssim.commands.args;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import org.enissay.dungeonssim.dungeon.system.DungeonDifficulty;

import java.util.Arrays;

public class DungeonDifficultyArgument extends ArgumentResolver<CommandSender, DungeonDifficulty> {

    @Override
    protected ParseResult<DungeonDifficulty> parse(Invocation<CommandSender> invocation, Argument<DungeonDifficulty> context, String argument) {
        DungeonDifficulty dungeonDifficulty = DungeonDifficulty.valueOf(argument);

        if (dungeonDifficulty != null) {
            return ParseResult.success(dungeonDifficulty);
        }

        return ParseResult.failure("Difficulty " + argument + " not found! (DIFFICULTY_NOT_FOUND)");
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<DungeonDifficulty> argument, SuggestionContext context) {
        return Arrays.stream(DungeonDifficulty.values()).toList().stream()
                .map(DungeonDifficulty::name)
                .collect(SuggestionResult.collector());
    }
}
