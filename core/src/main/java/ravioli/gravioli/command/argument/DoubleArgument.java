package ravioli.gravioli.command.argument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.ArgumentParseException;
import ravioli.gravioli.command.exception.number.NumberArgumentFormatException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.Optional;
import java.util.stream.DoubleStream;

public final class DoubleArgument<K> extends Argument<Double, DoubleArgument.DoubleArgumentParser<K>, K, DoubleArgument<K>> {
    public static <T> @NotNull DoubleArgument<T> of(@NotNull final String id) {
        return new DoubleArgument<>(id);
    }

    private final DoubleArgumentParser<K> parser;

    private DoubleArgument(@NotNull final String id) {
        super(id);

        this.parser = new DoubleArgumentParser<>(this);
    }

    @Override
    public @NotNull DoubleArgument.DoubleArgumentParser<K> getParser() {
        return this.parser;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DoubleArgumentParser<K> extends ArgumentParser<Double, K> {
        private final DoubleArgument<K> argument;

        @Override
        public boolean preParse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) {
            try {
                if (!inputQueue.hasNext()) {
                    return true;
                }
                inputQueue.readDouble();

                return true;
            } catch (final NumberFormatException e) {
                return false;
            }
        }

        @Override
        public @NotNull ArgumentParseResult<Double> parse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue) {
            final String input = inputQueue.readString();

            try {
                return ArgumentParseResult.success(
                    Double.parseDouble(input)
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

                        return DoubleStream.of(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
                            .filter(number -> String.valueOf(number).startsWith(input))
                            .mapToObj(number -> Suggestion.replaceBasic(input, String.valueOf(number)))
                            .toList();
                    }
                );
        }
    }
}
