package ravioli.gravioli.command.argument.command;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.CommandArgumentType;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.Collections;
import java.util.List;

@Getter
public class DoubleArgument<T> extends CommandArgument<T, Double> {
    public static <T> @NotNull DoubleFloatArgumentBuilder<T> of(@NotNull final String id) {
        return new DoubleFloatArgumentBuilder<>(id);
    }

    public static <T> @NotNull DoubleFloatArgumentBuilder<T> optional(@NotNull final String id) {
        return new DoubleFloatArgumentBuilder<T>(id)
            .optional(true);
    }

    private final Double minimum;
    private final Double maximum;
    private final boolean clamp;

    DoubleArgument(@NotNull final String id, @Nullable final Double minimum, @Nullable final Double maximum, final boolean clamp) {
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
            Double.parseDouble(input);

            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        try {
            Double.parseDouble(input);

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
        return Collections.singletonList(new Suggestion(input));
    }

    @Override
    public @Nullable Double parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        final double number = traverser.readDouble();

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
    public @NotNull CommandArgumentType getType() {
        return CommandArgumentType.DOUBLE;
    }

    @Override
    public boolean shouldDefaultSuggestionsToBrigadier() {
        return true;
    }

    public static final class DoubleFloatArgumentBuilder<T> extends CommandArgumentBuilder<T, Double, DoubleArgument<T>, DoubleFloatArgumentBuilder<T>> {
        private Double minimum;
        private Double maximum;
        private boolean clamp;

        private DoubleFloatArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        public @NotNull DoubleArgument.DoubleFloatArgumentBuilder<T> min(final double minimum) {
            this.minimum = minimum;

            return this;
        }

        public @NotNull DoubleArgument.DoubleFloatArgumentBuilder<T> max(final double maximum) {
            this.maximum = maximum;

            return this;
        }

        public @NotNull DoubleArgument.DoubleFloatArgumentBuilder<T> clamp(final boolean clamp) {
            this.clamp = clamp;

            return this;
        }

        @Override
        protected DoubleArgument<T> createArgument() {
            return new DoubleArgument<>(this.id, this.minimum, this.maximum, this.clamp);
        }
    }
}
