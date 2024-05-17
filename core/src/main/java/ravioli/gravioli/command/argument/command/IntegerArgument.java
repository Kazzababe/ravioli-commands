package ravioli.gravioli.command.argument.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;

import java.util.Collections;
import java.util.List;

public class IntegerArgument<T> extends CommandArgument<T, Integer> {
    public static <T> @NotNull IntegerArgumentBuilder<T> of(@NotNull final String id) {
        return new IntegerArgumentBuilder<>(id);
    }

    public static <T> @NotNull IntegerArgumentBuilder<T> optional(@NotNull final String id) {
        return new IntegerArgumentBuilder<T>(id)
            .optional(true);
    }

    private final Integer minimum;
    private final Integer maximum;
    private final boolean clamp;

    IntegerArgument(@NotNull final String id, @Nullable final Integer minimum, @Nullable final Integer maximum, final boolean clamp) {
        super(id);

        this.minimum = minimum;
        this.maximum = maximum;
        this.clamp = clamp;
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        if (input.isBlank()) {
            return true;
        }
        try {
            Integer.parseInt(input);

            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        try {
            Integer.parseInt(input);

            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        if (input.isBlank()) {
            return Collections.singletonList(new Suggestion("<" + this.getId() + ">"));
        }
        return Collections.singletonList(new Suggestion("1231233"));
    }

    @Override
    public @Nullable Integer parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        final int number = traverser.readInt();

        if (this.minimum != null && number < this.minimum) {
            if (this.clamp) {
                return this.minimum;
            }
            throw new CommandParseException("Input value \"" + number + "\" is lower than the minimum allowed value of " + this.minimum + ".");
        }
        if (this.maximum != null && number > this.maximum) {
            if (this.clamp) {
                return this.maximum;
            }
            throw new CommandParseException("Input value \"" + number + "\" is greater than the maximum allowed value of " + this.minimum + ".");
        }
        return number;
    }

    @Override
    public boolean shouldDefaultSuggestionsToBrigadier() {
        return true;
    }

    @Override
    public @NotNull ArgumentType<?> getBrigadierType() {
        if (this.minimum != null) {
            if (this.maximum != null) {
                return IntegerArgumentType.integer(this.minimum, this.maximum);
            }
            return IntegerArgumentType.integer(this.minimum);
        }
        return IntegerArgumentType.integer();
    }

    public static final class IntegerArgumentBuilder<T> extends CommandArgumentBuilder<T, Integer, IntegerArgument<T>, IntegerArgumentBuilder<T>> {
        private Integer minimum;
        private Integer maximum;
        private boolean clamp;

        private IntegerArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        public @NotNull IntegerArgumentBuilder<T> min(final int minimum) {
            this.minimum = minimum;

            return this;
        }

        public @NotNull IntegerArgumentBuilder<T> max(final int maximum) {
            this.maximum = maximum;

            return this;
        }

        public @NotNull IntegerArgumentBuilder<T> clamp(final boolean clamp) {
            this.clamp = clamp;

            return this;
        }

        @Override
        protected IntegerArgument<T> createArgument() {
            return new IntegerArgument<>(this.id, this.minimum, this.maximum, this.clamp);
        }
    }
}
