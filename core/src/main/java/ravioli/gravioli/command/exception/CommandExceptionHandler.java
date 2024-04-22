package ravioli.gravioli.command.exception;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;

@FunctionalInterface
public interface CommandExceptionHandler<T> {
    void handle(@NotNull CommandContext<T> commandContext, @NotNull CommandParseException exception);
}
