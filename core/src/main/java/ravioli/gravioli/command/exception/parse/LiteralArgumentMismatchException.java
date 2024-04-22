package ravioli.gravioli.command.exception.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.exception.ArgumentParseException;

@Getter
public final class LiteralArgumentMismatchException extends ArgumentParseException {
    private final String expectedLiteral;
    private final String providedLiteral;

    public LiteralArgumentMismatchException(@NotNull final String expectedLiteral, @NotNull final String providedLiteral) {
        super("Expected literal '" + expectedLiteral + "' did not match provided '" + providedLiteral + "'");

        this.expectedLiteral = expectedLiteral;
        this.providedLiteral = providedLiteral;
    }
}
