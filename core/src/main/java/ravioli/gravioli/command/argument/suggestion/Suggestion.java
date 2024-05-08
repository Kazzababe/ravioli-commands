package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;

// TODO: I'm leaving the methods for replacing text in as I think it's dumb paper doesn't support this by default so I'll attempt to fix
// Should note that I think they do support it but not if the suggestion contains whitespaces as you'll
// notice you can submit entire player names as suggestions but when you tab-complete it will replace existing text
// rather than insert the entire player name at your current cursor position
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
