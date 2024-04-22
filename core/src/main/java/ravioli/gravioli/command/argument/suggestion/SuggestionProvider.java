package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.List;

@FunctionalInterface
public interface SuggestionProvider<K> {
    @NotNull List<Suggestion> getSuggestions(@NotNull CommandContext<K> commandContext, @NotNull StringTraverser inputQueue);
}
