package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;

public record Suggestion(@NotNull String text, @NotNull String tooltip) {
    public Suggestion(@NotNull final String text) {
        this(text, text);
    }
}
