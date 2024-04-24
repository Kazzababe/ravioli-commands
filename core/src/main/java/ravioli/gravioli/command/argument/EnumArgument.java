package ravioli.gravioli.command.argument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.parse.EnumArgumentMismatchException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class EnumArgument<E extends Enum<E>, K> extends Argument<E, EnumArgument.EnumArgumentParser<E, K>, K, EnumArgument<E, K>> {
    public static <E extends Enum<E>, T> @NotNull EnumArgument<E, T> of(@NotNull final String id, @NotNull final Class<E> enumClass) {
        return new EnumArgument<>(id, enumClass);
    }

    private final EnumArgumentParser<E, K> parser;
    private final Class<E> enumClass;

    private EnumArgument(@NotNull final String id, @NotNull final Class<E> enumClass) {
        super(id);

        this.parser = new EnumArgumentParser<>(this, enumClass);
        this.enumClass = enumClass;
    }

    @Override
    public @NotNull EnumArgument.EnumArgumentParser<E, K> getParser() {
        return this.parser;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class EnumArgumentParser<E extends Enum<E>, K> extends ArgumentParser<E, K> {
        private final EnumArgument<E, K> argument;
        private final Class<E> enumClass;
        private final Set<String> enumValues;

        private EnumArgumentParser(@NotNull final EnumArgument<E, K> argument, @NotNull final Class<E> enumClass) {
            this.argument = argument;
            this.enumClass = enumClass;
            this.enumValues = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
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
        public @NotNull ArgumentParseResult<E> parse(@NotNull final CommandContext<K> commandContext, @NotNull final StringTraverser inputQueue, final boolean suggestions) {
            final String input = inputQueue.readString();

            if (this.enumValues.contains(input.toLowerCase())) {
                return ArgumentParseResult.success(Enum.valueOf(this.enumClass, input.toUpperCase()));
            }
            return ArgumentParseResult.failure(new EnumArgumentMismatchException(this.enumClass, input));
        }

        @Override
        public @NotNull SuggestionProvider<K> getSuggestionProvider() {
            return (context, inputQueue) -> {
                final String input = inputQueue.readString();
                final String lowerCaseInput = input.toLowerCase();

                return this.enumValues
                        .stream()
                        .filter(enumValue -> enumValue.startsWith(lowerCaseInput))
                        .map(enumValue -> Suggestion.replaceBasic(input, enumValue))
                        .toList();
            };
        }
    }
}