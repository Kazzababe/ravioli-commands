package ravioli.gravioli.command.exception;

import org.jetbrains.annotations.NotNull;

public class CommandParseException extends Exception {
    public CommandParseException(@NotNull final String message) {
        super(message);
    }
}
