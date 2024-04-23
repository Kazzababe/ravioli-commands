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

    private boolean allowNull;

    private PlayerArgument(final @NotNull String id) {
        super(id);

        this.parser = new PlayerArgumentParser(this);
    }

    public @NotNull PlayerArgument allowNull(final boolean allowNull) {
        this.allowNull = allowNull;

        return this;
    }

    @Override
    public int getPriority() {
        return 10;
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
        public @NotNull ArgumentParseResult<Player> parse(@NotNull final CommandContext<CommandSender> commandContext, @NotNull final StringTraverser inputQueue){
            final String input = inputQueue.readString();
            final Player player = Bukkit.getPlayerExact(input);

            if (player == null) {
                if (this.argument.allowNull) {
                    return ArgumentParseResult.ignore(null);
                }
                return ArgumentParseResult.failure(
                    new ArgumentParseException("Could not find player with name: " + input)
                );
            }
            return ArgumentParseResult.success(player);
        }

        @Override
        public @NotNull SuggestionProvider<CommandSender> getSuggestionProvider() {
            return Optional.ofNullable(this.argument.getSuggestionProvider())
                .orElseGet(() ->
                    (context, inputQueue) -> {
                        final String input = inputQueue.readString();
                        final String lowerCaseInput = input.toLowerCase();

                        return Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(lowerCaseInput))
                            .map(name -> Suggestion.replaceBasic(input, name))
                            .toList();
                    }
                );
        }
    }
}
