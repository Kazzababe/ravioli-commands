package ravioli.gravioli.command.argument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.ArgumentParseException;
import ravioli.gravioli.command.exception.parse.LiteralArgumentMismatchException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class LiteralArgument<K> extends Argument<String, LiteralArgument.LiteralArgumentParser<K>, K, LiteralArgument<K>> {
    public static <T> @NotNull LiteralArgument<T> of(@NotNull final String id) {
        return new LiteralArgument<>(id);
    }

    private final LiteralArgumentParser<K> parser;

    private LiteralArgument(final @NotNull String id) {
        super(id);

        this.parser = new LiteralArgumentParser<>(this, id.toLowerCase());
    }

    @Override
    public @NotNull String getUsageRepresentation() {
        return this.getId();
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public @NotNull LiteralArgumentParser<K> getParser() {
        return this.parser;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LiteralArgumentParser<K> extends ArgumentParser<String, K> {
        private final LiteralArgument<K> argument;
        private final String match;

        @Override
        public boolean preParse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) {
            final String next = inputQueue.readString().toLowerCase();

            return this.match.startsWith(next);
        }

        @Override
        public @NotNull ArgumentParseResult<String> parse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) throws ArgumentParseException {
            final String next = inputQueue.readString();

            if (next.equalsIgnoreCase(this.match)) {
                return ArgumentParseResult.success(next);
            }
            return ArgumentParseResult.failure(new LiteralArgumentMismatchException(this.match, next));
        }

        @Override
        public @NotNull SuggestionProvider<K> getSuggestionProvider() {
            return Optional.ofNullable(this.argument.getSuggestionProvider())
                .orElseGet(() ->
                    (context, inputQueue) -> {
                        final String input = inputQueue.readString().toLowerCase();

                        if (this.match.startsWith(input)) {
                            return Collections.singletonList(Suggestion.basic(this.match));
                        }
                        return Collections.emptyList();
                    }
                );
        }
    }
}
