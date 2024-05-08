package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Suggestion(@NotNull String replace, @NotNull String text, @NotNull String tooltip) {
    public static final String PRE_PROCESS_PREFIX = "__ravioli_commands__";
    public static final String PRE_PROCESS_DELIMITER = "⌶⌫⌶";

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final Suggestion that = (Suggestion) o;
        return Objects.equals(this.text, that.text) && Objects.equals(this.tooltip, that.tooltip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.text, this.tooltip);
    }

    public @NotNull String toProcessableFormat() {
        return PRE_PROCESS_PREFIX + PRE_PROCESS_DELIMITER + this.replace + PRE_PROCESS_DELIMITER + this.text + PRE_PROCESS_DELIMITER + this.tooltip;
    }
}
