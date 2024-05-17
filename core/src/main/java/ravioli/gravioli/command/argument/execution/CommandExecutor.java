package ravioli.gravioli.command.argument.execution;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;

@FunctionalInterface
public interface CommandExecutor<T> {
    void execute(@NotNull CommandContext<T> context);
}
