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
public class LongArgument<T> extends CommandArgument<T, Long> {
    public static <T> @NotNull LongArgumentBuilder<T> of(@NotNull final String id) {
        return new LongArgumentBuilder<>(id);
    }

    public static <T> @NotNull LongArgumentBuilder<T> optional(@NotNull final String id) {
        return new LongArgumentBuilder<T>(id)
            .optional(true);
    }

    private final Long minimum;
    private final Long maximum;
    private final boolean clamp;

    LongArgument(@NotNull final String id, @Nullable final Long minimum, @Nullable final Long maximum, final boolean clamp) {
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

        if (input.isBlank()) {
            return Collections.singletonList(new Suggestion("<" + this.getId() + ">"));
        }
        return Collections.singletonList(new Suggestion(input));
    }

    @Override
    public @Nullable Long parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        final long number = traverser.readLong();

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
    public @NotNull CommandArgumentType getType() {
        return CommandArgumentType.LONG;
    }

    public static final class LongArgumentBuilder<T> extends CommandArgumentBuilder<T, Long, LongArgument<T>, LongArgumentBuilder<T>> {
        private Long minimum;
        private Long maximum;
        private boolean clamp;

        private LongArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        public @NotNull LongArgument.LongArgumentBuilder<T> min(final long minimum) {
            this.minimum = minimum;

            return this;
        }

        public @NotNull LongArgument.LongArgumentBuilder<T> max(final long maximum) {
            this.maximum = maximum;

            return this;
        }

        public @NotNull LongArgument.LongArgumentBuilder<T> clamp(final boolean clamp) {
            this.clamp = clamp;

            return this;
        }

        @Override
        protected LongArgument<T> createArgument() {
            return new LongArgument<>(this.id, this.minimum, this.maximum, this.clamp);
        }
    }
}
