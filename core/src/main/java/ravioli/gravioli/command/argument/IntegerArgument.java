package ravioli.gravioli.command.argument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.number.NumberArgumentFormatException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.Optional;
import java.util.stream.IntStream;

public final class IntegerArgument<K> extends Argument<Integer, IntegerArgument.IntegerArgumentParser<K>, K, IntegerArgument<K>> {
    public static <T> @NotNull IntegerArgument<T> of(@NotNull final String id) {
        return new IntegerArgument<>(id);
    }

    private final IntegerArgumentParser<K> parser;

    private IntegerArgument(@NotNull final String id) {
        super(id);

        this.parser = new IntegerArgumentParser<>(this);
    }

    @Override
    public @NotNull IntegerArgument.IntegerArgumentParser<K> getParser() {
        return this.parser;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class IntegerArgumentParser<K> extends ArgumentParser<Integer, K> {
        private final IntegerArgument<K> argument;

        @Override
        public boolean preParse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) {
            try {
                if (!inputQueue.hasNext()) {
                    return true;
                }
                inputQueue.readInt();

                return true;
            } catch (final NumberFormatException e) {
                return false;
            }
        }

        @Override
        public boolean isOrCouldBeValid(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) {
            return this.preParse(commandContext, inputQueue);
        }

        @Override
        public @NotNull ArgumentParseResult<Integer> parse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue, final boolean suggestions) {
            final String input = inputQueue.readString();

            try {
                return ArgumentParseResult.success(
                    Integer.parseInt(input)
                );
            } catch (final NumberFormatException e) {
                return ArgumentParseResult.failure(
                    new NumberArgumentFormatException(input)
                );
            }
        }

        @Override
        public @NotNull SuggestionProvider<K> getSuggestionProvider() {
            return Optional.ofNullable(this.argument.getSuggestionProvider())
                .orElseGet(() ->
                    (context, inputQueue) -> {
                        final String input = inputQueue.readString();

                        return IntStream.rangeClosed(0, 9)
                            .filter(number -> String.valueOf(number).startsWith(input))
                            .mapToObj(number -> Suggestion.replaceBasic(input, String.valueOf(number)))
                            .toList();
                    }
                );
        }
    }
}
