package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;

public record Suggestion(@NotNull String text, @NotNull String tooltip) {
    public static @NotNull Suggestion basic(@NotNull final String text) {
        return new Suggestion(text, text);
    }

    public static @NotNull Suggestion text(@NotNull final String text) {
        return new Suggestion(text, "");
    }
}
