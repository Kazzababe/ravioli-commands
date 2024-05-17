package ravioli.gravioli.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.node.CommandNode;
import ravioli.gravioli.command.parse.CommandParseResult;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class CommandManager<T> {
    private final Map<String, Command<T>> registeredCommands = new HashMap<>();

    public void registerCommand(@NotNull final Command<T> command) {
        command.getAllAliases().forEach(alias -> {
            alias = this.formatCommandName(alias);

            if (this.registeredCommands.containsKey(alias)) {
                throw new IllegalArgumentException("Cannot register alias \"" + alias + "\" as it's been registered by another command already.");
            }
            this.registeredCommands.put(alias, command);
        });
    }

    public final @Nullable Command<T> getCommand(@NotNull final String name) {
        return this.registeredCommands.get(this.formatCommandName(name));
    }

    protected final @NotNull String formatCommandName(@NotNull final String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public final @NotNull CommandParseResult<T> parseCommand(@NotNull final T sender, @NotNull final String input) {
        final StringTraverser traverser = new StringTraverser(input);
        final CommandContext<T> context = new CommandContext<>(sender);
        final String commandName = this.formatCommandName(new StringTraverser(traverser).readString());
        final Command<T> command = this.registeredCommands.get(commandName);

        if (command == null) {
            return null;
//            return CommandParseResult.failure(new CommandParseException("Unknown command: " + commandName));
        }
        return this.parseNode(command, command.getRootNode(), context, traverser);
    }

    private @NotNull CommandParseResult<T> parseNode(
        @NotNull final Command<T> command,
        @NotNull final CommandNode<T, ?> node,
        @NotNull final CommandContext<T> context,
        @NotNull final StringTraverser traverser
    ) {
        if (!node.isValidForExecution(context, new StringTraverser(traverser))) {
            return CommandParseResult.failure(new CommandParseException("Unknown command: " + traverser.getContent().trim()));
        }
        final CommandArgument<T, ?> argument = node.getArgument();

        try {
            final Object value = argument.parse(context, traverser);

            if (value != null) {
                context.supply(argument.getId(), value);
            }
        } catch (final CommandParseException e) {
            return CommandParseResult.failure(e);
        }
        traverser.traverseWhitespace();

        if (!traverser.hasNext()) {
            if (command.canExecute(node)) {
                final String permission = command.getNodePermission(node);

                if (permission == null || this.hasPermission(context.getSender(), permission)) {
                    return CommandParseResult.success(() -> {
                        Objects.requireNonNull(command.getCommandExecutor(node)).execute(context);
                    });
                }
                return CommandParseResult.failure(new CommandParseException("Invalid permissions."));
            }
            return CommandParseResult.failure(new CommandParseException("Incomplete command."));
        }
        final List<CommandNode<T, ?>> children = node.getChildren();

        for (final CommandNode<T, ?> child : children) {
            final StringTraverser childTraverser = new StringTraverser(traverser);
            final CommandContext<T> childContext = new CommandContext<>(context);
            final CommandParseResult<T> childResult = this.parseNode(command, child, childContext, childTraverser);

            if (childResult.isSuccess()) {
                return childResult;
            }
        }
        return CommandParseResult.failure(new CommandParseException("Unrecognized argument: " + traverser.readString()));
    }

    public final @NotNull List<Suggestion> getSuggestions(@NotNull final T sender, @NotNull final String input) {
        final StringTraverser traverser = new StringTraverser(input);
        final CommandContext<T> context = new CommandContext<>(sender);
        final String commandName = this.formatCommandName(new StringTraverser(traverser).readString());
        final Command<T> command = this.registeredCommands.get(commandName);

        if (command == null) {
            return this.registeredCommands.entrySet()
                .stream()
                .filter(entry -> {
                    final Command<T> registeredCommand = entry.getValue();
                    final String permission = registeredCommand.getCommandMetadata().getPermission();

                    return permission == null || this.hasPermission(sender, permission);
                })
                .map(Map.Entry::getKey)
                .filter(alias -> alias.startsWith(commandName))
                .map(Suggestion::new)
                .toList();
        }
        return this.getSuggestionsForNode(command, command.getRootNode(), context, traverser);
    }

    private @NotNull List<Suggestion> getSuggestionsForNode(
        @NotNull final Command<T> command,
        @NotNull final CommandNode<T, ?> node,
        @NotNull final CommandContext<T> context,
        @NotNull final StringTraverser originalTraverser
    ) {
        if (!originalTraverser.hasNext()) {
            return this.getFilteredSuggestionsForNode(context, node, originalTraverser);
        }
        final StringTraverser traverser = new StringTraverser(originalTraverser);

        if (node.isValidForExecution(context, new StringTraverser(traverser))) {
            try {
                final Object value = node.getArgument().parse(context, traverser);

                if (value != null) {
                    context.supply(node.getArgument().getId(), value);
                }
                traverser.traverseWhitespace();

                if (!traverser.hasNext() && !traverser.endsInWhitespace()) {
                    if (node.isValidForSuggestions(context, new StringTraverser(originalTraverser))) {
                        return this.getFilteredSuggestionsForNode(context, node, originalTraverser);
                    }
                    return new ArrayList<>();
                }
            } catch (final CommandParseException e) {
                return new ArrayList<>();
            }
        } else {
            final StringTraverser checkTraverser = new StringTraverser(originalTraverser);

            if (!node.isValidForSuggestions(context, checkTraverser)) {
                return new ArrayList<>();
            }
            if (!checkTraverser.hasNext()) {
                return this.getFilteredSuggestionsForNode(context, node, originalTraverser);
            }
        }
        final List<Suggestion> suggestions = new ArrayList<>();

        for (final CommandNode<T, ?> child : node.getChildren()) {
            final StringTraverser childTraverser = new StringTraverser(traverser);

            suggestions.addAll(this.getSuggestionsForNode(command, child, context, childTraverser));
        }
        return suggestions;
    }

    private @NotNull List<Suggestion> getFilteredSuggestionsForNode(
        @NotNull final CommandContext<T> context,
        @NotNull final CommandNode<T, ?> node,
        @NotNull final StringTraverser input
    ) {
        if (this.hasAnyPermissions(context.getSender(), node)) {
            return node.getArgument().getSuggestions(context, input);
        }
        return new ArrayList<>();
    }

    private boolean hasAnyPermissions(@NotNull final T sender, @NotNull final CommandNode<T, ?> node) {
        final Set<String> childrenPermissions = node.getChildrenPermissions();

        if (childrenPermissions.isEmpty()) {
            return true;
        }
        for (final String childrenPermission : childrenPermissions) {
            if (this.hasPermission(sender, childrenPermission)) {
                return true;
            }
        }
        return false;
    }

    protected abstract boolean hasPermission(@NotNull T sender, @NotNull String permission);
}
