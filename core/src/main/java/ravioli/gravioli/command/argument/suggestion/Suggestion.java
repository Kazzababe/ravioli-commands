package ravioli.gravioli.command.argument.suggestion;

import com.mojang.brigadier.context.StringRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Suggestion(@NotNull String text, @NotNull String tooltip, @Nullable StringRange range) {
    public Suggestion(@NotNull final String text, @NotNull final String tooltip) {
        this(text, tooltip, null);
    }

    public Suggestion(@NotNull final String text) {
        this(text, text, null);
    }
}
