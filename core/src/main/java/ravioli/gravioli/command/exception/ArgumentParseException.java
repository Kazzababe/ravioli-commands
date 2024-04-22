package ravioli.gravioli.command.exception;

import org.jetbrains.annotations.NotNull;

public class ArgumentParseException extends Exception {
    public ArgumentParseException(@NotNull final String message) {
        super(message);
    }
}
