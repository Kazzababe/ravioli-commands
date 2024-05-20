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
import java.util.Locale;

public class LiteralArgument<T> extends CommandArgument<T, Void> {
    public static <T> @NotNull LiteralArgumentBuilder<T> of(@NotNull final String id) {
        return new LiteralArgumentBuilder<>(id);
    }

    public static <T> @NotNull LiteralArgumentBuilder<T> optional(@NotNull final String id) {
        return new LiteralArgumentBuilder<T>(id)
            .optional(true);
    }

    @Getter
    private final String lowerCaseLabel;

    LiteralArgument(@NotNull final String id, @NotNull final String label) {
        super(id);

        this.lowerCaseLabel = label.toLowerCase(Locale.ROOT);
    }

    LiteralArgument(@NotNull final String id) {
        this(id, id);
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString().toLowerCase(Locale.ROOT);

        return this.lowerCaseLabel.startsWith(input);
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        return this.lowerCaseLabel.equalsIgnoreCase(input);
    }

    @Override
    public @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        return Collections.singletonList(
            new Suggestion(this.lowerCaseLabel)
        );
    }

    @Override
    public @Nullable Void parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        traverser.readString();

        return null;
    }

    @Override
    public @NotNull CommandArgumentType getType() {
        return CommandArgumentType.LITERAL;
    }

    public static final class LiteralArgumentBuilder<T> extends CommandArgumentBuilder<T, Void, LiteralArgument<T>, LiteralArgumentBuilder<T>> {
        private LiteralArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        @Override
        protected LiteralArgument<T> createArgument() {
            return new LiteralArgument<>(this.id);
        }
    }
}
