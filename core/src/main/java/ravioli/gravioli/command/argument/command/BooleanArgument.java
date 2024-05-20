package ravioli.gravioli.command.argument.command;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.CommandArgumentType;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.List;

@Getter
public class BooleanArgument<T> extends CommandArgument<T, Boolean> {
    public static <T> @NotNull BooleanArgumentBuilder<T> of(@NotNull final String id) {
        return new BooleanArgumentBuilder<>(id);
    }

    public static <T> @NotNull BooleanArgumentBuilder<T> optional(@NotNull final String id) {
        return new BooleanArgumentBuilder<T>(id)
            .optional(true);
    }

    private static final List<String> FLAGS = List.of("true", "false");

    BooleanArgument(@NotNull final String id) {
        super(id);
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        if (input.isBlank()) {
            return true;
        }
        try {
            Long.parseLong(input);

            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        try {
            Long.parseLong(input);

            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();
        final String lowerCaseInput = input.toLowerCase();

        return FLAGS.stream()
            .filter(flag -> flag.startsWith(lowerCaseInput))
            .map(Suggestion::new)
            .toList();
    }

    @Override
    public @Nullable Boolean parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        try {
            return traverser.readBoolean();
        } catch (final IllegalArgumentException e) {
            throw new CommandParseException("Input value is not a boolean.");
        }
    }

    @Override
    public boolean shouldPrioritizeNativeSuggestions() {
        return true;
    }

    @Override
    public @NotNull CommandArgumentType getType() {
        return CommandArgumentType.BOOLEAN;
    }

    public static final class BooleanArgumentBuilder<T> extends CommandArgumentBuilder<T, Boolean, BooleanArgument<T>, BooleanArgumentBuilder<T>> {
        private BooleanArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        @Override
        protected BooleanArgument<T> createArgument() {
            return new BooleanArgument<>(this.id);
        }
    }
}
