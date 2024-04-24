package ravioli.gravioli.command.parse.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.exception.ArgumentParseException;

import java.util.Optional;

public final class ArgumentParseResult<T> {
    public static <T> @NotNull ArgumentParseResult<T> success(@Nullable final T value) {
        return new ArgumentParseResult<>(ParseResult.SUCCESS, value, null);
    }

    public static <T> @NotNull ArgumentParseResult<T> processSuggestions() {
        return new ArgumentParseResult<>(ParseResult.PROGRESS_SUGGESTIONS, null, null);
    }

    public static <T> @NotNull ArgumentParseResult<T> failure(@NotNull final ArgumentParseException exception) {
        return new ArgumentParseResult<>(ParseResult.FAILURE, null, exception);
    }

    private final ParseResult result;
    private final T value;
    private final ArgumentParseException exception;

    private ArgumentParseResult(@NotNull final ParseResult result, @Nullable final T value, @Nullable final ArgumentParseException exception) {
        this.result = result;
        this.value = value;
        this.exception = exception;
    }

    public @NotNull ParseResult getResult() {
        return this.result;
    }

    public @NotNull Optional<@NotNull T> getValue() {
        return Optional.ofNullable(this.value);
    }

    public @NotNull Optional<@NotNull ArgumentParseException> getException() {
        return Optional.ofNullable(this.exception);
    }

    public enum ParseResult {
        SUCCESS,
        FAILURE,
        PROGRESS_SUGGESTIONS;

        public boolean isSuccess() {
            return this == SUCCESS;
        }
    }
}
