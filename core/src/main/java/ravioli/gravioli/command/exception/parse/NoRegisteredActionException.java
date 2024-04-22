package ravioli.gravioli.command.exception.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.CommandNode;
import ravioli.gravioli.command.exception.CommandParseException;

@Getter
public final class NoRegisteredActionException extends CommandParseException {
    private final String command;

    public NoRegisteredActionException(@Nullable final CommandNode<?, ?> node, @NotNull final String command) {
        super(node, "No action registered for the valid command: " + command);

        this.command = command;
    }
}
