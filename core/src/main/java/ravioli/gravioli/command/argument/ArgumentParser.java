package ravioli.gravioli.command.argument;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.ArgumentParseException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.List;

public abstract class ArgumentParser<T, K> {
    public abstract boolean preParse(@NotNull CommandContext<K> commandContext, @NotNull StringTraverser inputQueue);

    public abstract @NotNull ArgumentParseResult<T> parse(@NotNull CommandContext<K> commandContext, @NotNull StringTraverser inputQueue) throws ArgumentParseException;

    public abstract @NotNull SuggestionProvider<K> getSuggestionProvider();
}
