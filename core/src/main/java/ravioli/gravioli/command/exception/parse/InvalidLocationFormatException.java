package ravioli.gravioli.command.exception.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.exception.ArgumentParseException;

@Getter
public final class InvalidLocationFormatException extends ArgumentParseException {
    private final String invalidInput;

    public InvalidLocationFormatException(@NotNull final String invalidInput) {
        super("Location coordinate is not formatted correctly: " + invalidInput);

        this.invalidInput = invalidInput;
    }
}
