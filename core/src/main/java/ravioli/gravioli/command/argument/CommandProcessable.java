package ravioli.gravioli.command.argument;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.parse.StringTraverser;

public interface CommandProcessable<T> {
    boolean isValidForSuggestions(@NotNull CommandContext<T> context, @NotNull StringTraverser traverser);

    boolean isValidForExecution(@NotNull CommandContext<T> context, @NotNull StringTraverser traverser);
}
