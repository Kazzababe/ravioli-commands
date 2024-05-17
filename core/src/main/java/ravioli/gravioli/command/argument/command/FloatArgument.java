package ravioli.gravioli.command.argument.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;

import java.util.Collections;
import java.util.List;

public class FloatArgument<T> extends CommandArgument<T, Float> {
    public static <T> @NotNull FloatArgumentBuilder<T> of(@NotNull final String id) {
        return new FloatArgumentBuilder<>(id);
    }

    public static <T> @NotNull FloatArgumentBuilder<T> optional(@NotNull final String id) {
        return new FloatArgumentBuilder<T>(id)
            .optional(true);
    }

    private final Float minimum;
    private final Float maximum;
    private final boolean clamp;

    FloatArgument(@NotNull final String id, @Nullable final Float minimum, @Nullable final Float maximum, final boolean clamp) {
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
            Float.parseFloat(input);

            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        try {
            Float.parseFloat(input);

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
    public @Nullable Float parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        final float number = traverser.readFloat();

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
                return FloatArgumentType.floatArg(this.minimum, this.maximum);
            }
            return FloatArgumentType.floatArg(this.minimum);
        }
        return FloatArgumentType.floatArg();
    }

    public static final class FloatArgumentBuilder<T> extends CommandArgumentBuilder<T, Float, FloatArgument<T>, FloatArgumentBuilder<T>> {
        private Float minimum;
        private Float maximum;
        private boolean clamp;

        private FloatArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        public @NotNull FloatArgument.FloatArgumentBuilder<T> min(final float minimum) {
            this.minimum = minimum;

            return this;
        }

        public @NotNull FloatArgument.FloatArgumentBuilder<T> max(final float maximum) {
            this.maximum = maximum;

            return this;
        }

        public @NotNull FloatArgument.FloatArgumentBuilder<T> clamp(final boolean clamp) {
            this.clamp = clamp;

            return this;
        }

        @Override
        protected FloatArgument<T> createArgument() {
            return new FloatArgument<>(this.id, this.minimum, this.maximum, this.clamp);
        }
    }
}
