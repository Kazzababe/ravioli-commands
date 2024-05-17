package ravioli.gravioli.command.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.exception.CommandParseException;

import java.util.Optional;


public final class CommandParseResult<T> {
    public static <T> @NotNull CommandParseResult<T> success(@NotNull final Runnable commandExecution) {
        return new CommandParseResult<>(CommandParseResult.ParseResult.SUCCESS, commandExecution, null);
    }

    public static <T> @NotNull CommandParseResult<T> failure(@NotNull final CommandParseException exception) {
        return new CommandParseResult<>(ParseResult.FAILURE, () -> {}, exception);
    }

    @Getter
    private final CommandParseResult.ParseResult result;

    @Getter
    private final Runnable commandExecution;

    private final CommandParseException exception;

    private CommandParseResult(
        @NotNull final CommandParseResult.ParseResult result,
        @NotNull final Runnable commandExecution,
        @Nullable final CommandParseException exception
    ) {
        this.result = result;
        this.commandExecution = commandExecution;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return this.result == ParseResult.SUCCESS;
    }

    public @NotNull Optional<@NotNull CommandParseException> getException() {
        return Optional.ofNullable(this.exception);
    }

    public enum ParseResult {
        SUCCESS,
        FAILURE;
    }
}
