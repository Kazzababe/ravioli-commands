package ravioli.gravioli.command.argument.suggestion;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.List;
import java.util.function.Supplier;

@FunctionalInterface
public interface SuggestionProvider<T> {
    static <T> @NotNull SuggestionProvider<T> staticCollection(@NotNull final List<String> suggestions) {
        return (commandContext, inputQueue) -> {
            final String input = inputQueue.readString();
            final String lowerCaseInput = input.toLowerCase();

            return suggestions.stream()
                .filter(suggestion -> suggestion.toLowerCase().startsWith(lowerCaseInput))
                .map(Suggestion::new)
                .toList();
        };
    }

    static <T> @NotNull SuggestionProvider<T> dynamicCollection(@NotNull final Supplier<@NotNull List<String>> suggestions) {
        return (commandContext, inputQueue) -> {
            final String input = inputQueue.readString();
            final String lowerCaseInput = input.toLowerCase();

            return suggestions.get()
                .stream()
                .distinct()
                .filter(suggestion -> suggestion.toLowerCase().startsWith(lowerCaseInput))
                .map(Suggestion::new)
                .toList();
        };
    }

    @NotNull List<Suggestion> apply(@NotNull CommandContext<T> context, @NotNull StringTraverser input);
}
