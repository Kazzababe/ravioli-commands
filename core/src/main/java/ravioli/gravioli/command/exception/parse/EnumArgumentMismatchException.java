package ravioli.gravioli.command.exception.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.exception.ArgumentParseException;

@Getter
public final class EnumArgumentMismatchException extends ArgumentParseException {
    private final Class<? extends Enum<?>> enumClass;
    private final String input;

    public EnumArgumentMismatchException(@NotNull final Class<? extends Enum<?>> enumClass, @NotNull final String input) {
        super("Input \"" + input + "\" does not match any values of enum " + enumClass.getSimpleName() + ".");

        this.enumClass = enumClass;
        this.input = input;
    }
}