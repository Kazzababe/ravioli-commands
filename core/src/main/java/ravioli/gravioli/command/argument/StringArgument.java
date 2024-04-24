package ravioli.gravioli.command.argument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.ArgumentParseException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.Collections;
import java.util.Optional;

public final class StringArgument<K> extends Argument<String, StringArgument.StringArgumentParser<K>, K, StringArgument<K>> {
    public static <T> @NotNull StringArgument<T> of(@NotNull final String id, @NotNull final StringMode stringMode) {
        return new StringArgument<>(id, stringMode);
    }

    public static <T> @NotNull StringArgument<T> of(@NotNull final String id) {
        return new StringArgument<>(id, StringMode.WORD);
    }

    private final StringArgumentParser<K> parser;
    private final StringMode stringMode;

    private StringArgument(@NotNull final String id, @NotNull final StringMode stringMode) {
        super(id);

        this.parser = new StringArgumentParser<>(this);
        this.stringMode = stringMode;
    }

    @Override
    public @NotNull StringArgument.StringArgumentParser<K> getParser() {
        return this.parser;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class StringArgumentParser<K> extends ArgumentParser<String, K> {
        private final StringArgument<K> argument;

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
            if (!inputQueue.hasNext()) {
                return ArgumentParseResult.success(null);
            }
            try {
                final String input = switch (this.argument.stringMode) {
                    case WORD -> inputQueue.readString();
                    case GREEDY -> inputQueue.readGreedyString();
                    case QUOTED -> inputQueue.readWrappedString('"', false);
                };

                return ArgumentParseResult.success(input);
            } catch (final IllegalArgumentException e) {
                return ArgumentParseResult.failure(
                    new ArgumentParseException("Quoted string argument did not start was not properly enclosed in quotations.")
                );
            }
        }

        @Override
        public @NotNull SuggestionProvider<K> getSuggestionProvider() {
            return Optional.ofNullable(this.argument.getSuggestionProvider())
                .orElseGet(() ->
                    (context, inputQueue) -> {
                        if (!inputQueue.hasNext()) {
                            return Collections.singletonList(
                                Suggestion.text(this.argument.getUsageRepresentation())
                            );
                        }
                        return Collections.emptyList();
                    }
                );
        }
    }

    public enum StringMode {
        WORD,
        GREEDY,
        QUOTED;
    }
}
