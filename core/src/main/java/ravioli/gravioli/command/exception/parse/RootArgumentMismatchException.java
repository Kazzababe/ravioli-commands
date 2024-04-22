package ravioli.gravioli.command.exception.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.exception.ArgumentParseException;

@Getter
public final class RootArgumentMismatchException extends ArgumentParseException {
    private final String input;

    public RootArgumentMismatchException(@NotNull final String input) {
        super("No root argument or aliases matched the provided input: " + input);

        this.input = input;
    }
}
