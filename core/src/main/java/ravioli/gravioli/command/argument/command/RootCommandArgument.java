package ravioli.gravioli.command.argument.command;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.metadata.CommandMetadata;

public final class RootCommandArgument<T> extends LiteralArgument<T> {
    public RootCommandArgument(@NotNull final CommandMetadata<T> metadata) {
        super("root", metadata.getName());
    }
}
