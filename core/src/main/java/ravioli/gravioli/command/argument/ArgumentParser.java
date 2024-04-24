package ravioli.gravioli.command.argument;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

public abstract class ArgumentParser<T, K> {
    public abstract boolean preParse(@NotNull CommandContext<K> commandContext, @NotNull StringTraverser inputQueue);

    public abstract boolean isOrCouldBeValid(@NotNull CommandContext<K> commandContext, @NotNull StringTraverser inputQueue);

    public abstract @NotNull ArgumentParseResult<T> parse(@NotNull CommandContext<K> commandContext, @NotNull StringTraverser inputQueue, boolean suggestions);

    public abstract @NotNull SuggestionProvider<K> getSuggestionProvider();
}
