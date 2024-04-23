package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;

public record Suggestion(@NotNull String replace, @NotNull String text, @NotNull String tooltip) {
    public static @NotNull Suggestion basic(@NotNull final String text) {
        return new Suggestion("", text, text);
    }

    public static @NotNull Suggestion replaceBasic(@NotNull final String replace, @NotNull final String text) {
        return new Suggestion(replace, text, text);
    }

    public static @NotNull Suggestion text(@NotNull final String text) {
        return new Suggestion("", text, "");
    }

    public static @NotNull Suggestion replaceText(@NotNull final String replace, @NotNull final String text) {
        return new Suggestion(replace, text, "");
    }
}
