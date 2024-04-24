package ravioli.gravioli.command.argument;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.parse.RootArgumentMismatchException;
import ravioli.gravioli.command.metadata.CommandMetadata;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class RootArgument<K> extends Argument<String, RootArgument.RootArgumentParser<K>, K, RootArgument<K>> {
    private final RootArgumentParser<K> parser;
    private final CommandMetadata<K> metadata;

    public RootArgument(@NotNull final CommandMetadata<K> metadata) {
        super("root");

        this.metadata = metadata;
        this.parser = new RootArgumentParser<>(this);
    }

    @Override
    public @NotNull String getUsageRepresentation() {
        return this.metadata.getName();
    }

    @Override
    public @NotNull RootArgument.RootArgumentParser<K> getParser() {
        return this.parser;
    }

    public static final class RootArgumentParser<K> extends ArgumentParser<String, K> {
        private final RootArgument<K> argument;
        private final Set<String> matches;

        private RootArgumentParser(@NotNull final RootArgument<K> argument) {
            this.argument = argument;

            final CommandMetadata<K> metadata = argument.metadata;
            final String[] aliases = metadata.getAliases();

            this.matches = new HashSet<>();
            this.matches.add(metadata.getName().toLowerCase());

            for (final String alias : aliases) {
                this.matches.add(alias.toLowerCase());
            }
        }

        @Override
        public boolean preParse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) {
            return true;
        }

        @Override
        public boolean isOrCouldBeValid(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) {
            return this.preParse(commandContext, inputQueue);
        }

        @Override
        public @NotNull ArgumentParseResult<String> parse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue, final boolean suggestions) {
            final String input = inputQueue.readString();

            if (this.matches.contains(input.toLowerCase())) {
                return ArgumentParseResult.success(input);
            }
            return ArgumentParseResult.failure(new RootArgumentMismatchException(input));
        }

        @Override
        public @NotNull SuggestionProvider<K> getSuggestionProvider() {
            return Optional.ofNullable(this.argument.getSuggestionProvider())
                .orElseGet(() ->
                    (context, inputQueue) -> {
                        final String input = inputQueue.readString();
                        final String lowerCaseInput = input.toLowerCase();

                        return this.matches
                            .stream()
                            .filter(match -> match.startsWith(lowerCaseInput))
                            .map(match -> Suggestion.replaceBasic(input, match))
                            .toList();
                    }
                );
        }
    }
}
