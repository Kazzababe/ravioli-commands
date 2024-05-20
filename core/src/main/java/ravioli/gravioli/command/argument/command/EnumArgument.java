package ravioli.gravioli.command.argument.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.CommandArgumentType;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EnumArgument<T, E extends Enum<E>> extends CommandArgument<T, E> {
    public static <T, K extends Enum<K>> @NotNull EnumArgumentBuilder<T, K> of(@NotNull final String id, @NotNull final Class<K> enumClass) {
        return new EnumArgumentBuilder<>(id, enumClass);
    }

    public static <T, K extends Enum<K>> @NotNull EnumArgumentBuilder<T, K> optional(@NotNull final String id, @NotNull final Class<K> enumClass) {
        return new EnumArgumentBuilder<T, K>(id, enumClass)
            .optional(true);
    }
    private final Class<E> enumClass;
    private final List<String> enumValues;

    EnumArgument(@NotNull final String id, @NotNull final Class<E> enumClass) {
        super(id);

        this.enumClass = enumClass;
        this.enumValues = Arrays.stream(enumClass.getEnumConstants())
            .map(enumValue -> enumValue.name().toLowerCase(Locale.ROOT))
            .toList();
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString().toLowerCase(Locale.ROOT);

        for (final String enumValue : this.enumValues) {
            if (enumValue.startsWith(input)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString().toLowerCase(Locale.ROOT);

        return this.enumValues.contains(input);
    }

    @Override
    public @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString().toLowerCase(Locale.ROOT);

        return this.enumValues
            .stream()
            .filter(enumValue -> enumValue.startsWith(input))
            .map(Suggestion::new)
            .toList();
    }

    @Override
    public @Nullable E parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString().toUpperCase(Locale.ROOT);

        return Enum.valueOf(this.enumClass, input);
    }

    @Override
    public @NotNull CommandArgumentType getType() {
        return CommandArgumentType.WORD;
    }

    public static final class EnumArgumentBuilder<T, E extends Enum<E>> extends CommandArgumentBuilder<T, E, EnumArgument<T, E>, EnumArgumentBuilder<T, E>> {
        private final Class<E> enumClass;

        private EnumArgumentBuilder(@NotNull final String id, @NotNull final Class<E> enumClass) {
            super(id);

            this.enumClass = enumClass;
        }

        @Override
        protected EnumArgument<T, E> createArgument() {
            return new EnumArgument<>(this.id, this.enumClass);
        }
    }
}
