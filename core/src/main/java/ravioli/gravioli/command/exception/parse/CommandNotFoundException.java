package ravioli.gravioli.command.exception.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.CommandNode;
import ravioli.gravioli.command.exception.CommandParseException;

@Getter
public final class CommandNotFoundException extends CommandParseException {
    private final String commandAlias;

    public CommandNotFoundException(@Nullable final CommandNode<?, ?> node, @NotNull final String commandAlias) {
        super(node, "Command not found for alias: " + commandAlias);

        this.commandAlias = commandAlias;
    }
}
