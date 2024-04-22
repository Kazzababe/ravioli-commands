package ravioli.gravioli.command.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.CommandNode;

@Getter
public class CommandParseException extends Exception {
    private final CommandNode<?, ?> node;

    public CommandParseException(@Nullable final CommandNode<?, ?> node, @NotNull final String message) {
        super(message);

        this.node = node;
    }
}
