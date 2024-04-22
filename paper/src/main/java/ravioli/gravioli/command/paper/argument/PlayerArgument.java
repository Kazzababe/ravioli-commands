package ravioli.gravioli.command.paper.argument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.Argument;
import ravioli.gravioli.command.argument.ArgumentParser;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.ArgumentParseException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.Optional;

public final class PlayerArgument extends Argument<Player, PlayerArgument.PlayerArgumentParser, CommandSender, PlayerArgument> {
    public static @NotNull PlayerArgument of(@NotNull final String id) {
        return new PlayerArgument(id);
    }

    private final PlayerArgumentParser parser;

    private PlayerArgument(final @NotNull String id) {
        super(id);

        this.parser = new PlayerArgumentParser(this);
    }

    @Override
    public @NotNull PlayerArgument.PlayerArgumentParser getParser() {
        return this.parser;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PlayerArgumentParser extends ArgumentParser<Player, CommandSender> {
        private final PlayerArgument argument;

        @Override
        public boolean preParse(@NotNull final CommandContext<CommandSender> commandContext, @NotNull final StringTraverser inputQueue) {
            return true;
        }

        @Override
        public @NotNull ArgumentParseResult<Player> parse(@NotNull final CommandContext<CommandSender> commandContext, @NotNull final StringTraverser inputQueue) throws ArgumentParseException {
            final String input = inputQueue.readString();

            return ArgumentParseResult.success(Bukkit.getPlayer(input));
        }

        @Override
        public @NotNull SuggestionProvider<CommandSender> getSuggestionProvider() {
            return Optional.ofNullable(this.argument.getSuggestionProvider())
                .orElseGet(() ->
                    (context, inputQueue) -> {
                        final String input = inputQueue.readString()
                            .toLowerCase();

                        return Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(input))
                            .map(Suggestion::text)
                            .toList();
                    }
                );
        }
    }
}
