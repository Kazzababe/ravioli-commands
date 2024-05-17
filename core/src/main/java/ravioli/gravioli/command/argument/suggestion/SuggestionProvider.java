package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.List;

@FunctionalInterface
public interface SuggestionProvider<T> {
    @NotNull List<Suggestion> apply(@NotNull CommandContext<T> context, @NotNull StringTraverser input);
}
