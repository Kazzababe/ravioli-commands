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
public class StringArgument<T> extends CommandArgument<T, String> {
    public static <T> @NotNull StringArgumentBuilder<T> of(@NotNull final String id) {
        return new StringArgumentBuilder<>(id);
    }

    public static <T> @NotNull StringArgumentBuilder<T> optional(@NotNull final String id) {
        return new StringArgumentBuilder<T>(id)
            .optional(true);
    }

    private final StringMode stringMode;

    StringArgument(@NotNull final String id, @NotNull final StringMode stringMode) {
        super(id);

        this.stringMode = stringMode;
    }

    @Override
    public boolean shouldPrioritizeNativeSuggestions() {
        return true;
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        return true;
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        final String input = traverser.readString();

        return !input.isBlank();
    }

    @Override
    public @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        return Collections.singletonList(new Suggestion("[<" + this.getId() + ">]"));
    }

    @Override
    public @Nullable String parse(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        return switch (this.stringMode) {
            case GREEDY -> traverser.readGreedyString();
            case QUOTES -> traverser.readWrappedString('"', true);
            case WORD -> traverser.readString();
        };
    }

    @Override
    public @NotNull CommandArgumentType getType() {
        return switch (this.stringMode) {
            case GREEDY -> CommandArgumentType.GREEDY_STRING;
            case QUOTES -> CommandArgumentType.QUOTED_STRING;
            case WORD -> CommandArgumentType.WORD;
        };
    }

    public static final class StringArgumentBuilder<T> extends CommandArgumentBuilder<T, String, StringArgument<T>, StringArgumentBuilder<T>> {
        private StringMode stringMode;

        private StringArgumentBuilder(@NotNull final String id) {
            super(id);

            this.stringMode = StringMode.WORD;
        }

        public @NotNull StringArgumentBuilder<T> stringMode(@NotNull final StringMode stringMode) {
            this.stringMode = stringMode;

            return this;
        }

        @Override
        protected StringArgument<T> createArgument() {
            return new StringArgument<>(this.id, this.stringMode);
        }
    }

    public enum StringMode {
        GREEDY,
        QUOTES,
        WORD;
    }
}
