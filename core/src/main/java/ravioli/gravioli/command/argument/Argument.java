package ravioli.gravioli.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;

public abstract class Argument<T, P extends ArgumentParser<T, K>, K, S extends Argument<T, P, K, S>> {
    private final String id;

    private SuggestionProvider<K> suggestionProvider;

    public Argument(@NotNull final String id) {
        this.id = id;
    }

    public final @NotNull String getId() {
        return this.id;
    }

    public final @Nullable SuggestionProvider<K> getSuggestionProvider() {
        return this.suggestionProvider;
    }

    public @NotNull S setSuggestionProvider(@NotNull final SuggestionProvider<K> suggestionProvider) {
        this.suggestionProvider = suggestionProvider;

        return (S) this;
    }

    public int getPriority() {
        return 0;
    }

    public @NotNull String getUsageRepresentation() {
        return "<" + this.id + ">";
    }

    public abstract @NotNull P getParser();

    public static abstract class Builder<T extends Builder<T, P, K>, P extends Argument<?, ?, K, P>, K> {
        protected final String id;

        protected SuggestionProvider<K> suggestionProvider;

        public Builder(@NotNull final String id) {
            this.id = id;
        }

        public @NotNull Builder<T, P, K> suggestionsProvider(@NotNull final SuggestionProvider<K> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;

            return this;
        }

        public abstract @NotNull P build();
    }
}
