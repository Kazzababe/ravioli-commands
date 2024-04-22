package ravioli.gravioli.command.exception.number;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.exception.ArgumentParseException;

public final class NumberArgumentFormatException extends ArgumentParseException {
    public NumberArgumentFormatException(final @NotNull String input) {
        super("Poorly formatted number: " + input);
    }
}
