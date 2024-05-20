package ravioli.gravioli.command.argument.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.CommandArgumentType;
import ravioli.gravioli.command.argument.CommandProcessable;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
public abstract class CommandArgument<T, K> implements CommandProcessable<T> {
    private final String id;

    private String description;
    private boolean optional;
    private SuggestionProvider<T> suggestionProvider;

    public CommandArgument(@NotNull final String id) {
        this.id = id;
    }

    @NotNull
    public final List<Suggestion> getSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        if (this.suggestionProvider != null) {
            return this.suggestionProvider.apply(context, traverser);
        }
        return this.parseSuggestions(context, traverser);
    }

    public boolean shouldPrioritizeNativeSuggestions() {
        return false;
    }

    @NotNull protected abstract List<Suggestion> parseSuggestions(@NotNull CommandContext<T> context, @NotNull StringTraverser traverser);

    @Nullable public abstract K parse(@NotNull CommandContext<T> context, @NotNull StringTraverser traverser) throws CommandParseException;

    @NotNull public abstract CommandArgumentType getType();

    public static abstract class CommandArgumentBuilder<T, K, A extends CommandArgument<T, K>, B extends CommandArgumentBuilder<T, K, A, B>> {
        protected final String id;
        protected String description;
        protected boolean optional;
        protected SuggestionProvider<T> suggestionProvider;

        protected CommandArgumentBuilder(final String id) {
            this.id = id;
            this.description = "";
            this.optional = false;
            this.suggestionProvider = null;
        }

        public @NotNull B description(@NotNull final String description) {
            this.description = description;

            return (B) this;
        }

        public @NotNull B optional(final boolean optional) {
            this.optional = optional;

            return (B) this;
        }

        public @NotNull B suggestionProvider(@NotNull final SuggestionProvider<T> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;

            return (B) this;
        }

        public @NotNull A build() {
            final A argument = this.createArgument();

            argument.setDescription(this.description);
            argument.setOptional(this.optional);
            argument.setSuggestionProvider(this.suggestionProvider);

            return argument;
        }

        protected abstract A createArgument();
    }
}
