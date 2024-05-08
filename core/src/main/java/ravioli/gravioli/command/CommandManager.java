package ravioli.gravioli.command;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.Argument;
import ravioli.gravioli.command.argument.ArgumentParser;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.ArgumentParseException;
import ravioli.gravioli.command.exception.CommandExceptionHandler;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.exception.parse.CommandNotFoundException;
import ravioli.gravioli.command.exception.parse.NoRegisteredActionException;
import ravioli.gravioli.command.exception.parse.PreparseFailureException;
import ravioli.gravioli.command.exception.permission.InsufficientPermissionsException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;
import ravioli.gravioli.command.parse.result.CommandParseResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class CommandManager<T> {
    private static final System.Logger LOGGER = System.getLogger("CommandManager");

    protected final Map<String, Command<T>> registeredCommands = new HashMap<>();

    @Getter
    @Setter
    private CommandExceptionHandler<T> defaultExceptionHandler = (context, exception) -> {
        LOGGER.log(System.Logger.Level.WARNING, exception.getMessage(), exception);
    };

    @Getter
    @Setter
    private Executor defaultExecutor;

    public final @Nullable Command<T> getCommand(@NotNull final String name) {
        return this.registeredCommands.get(name.toLowerCase());
    }

    public void registerCommand(@NotNull final Command<T> command) {
        command.getAllAliases().forEach(alias -> {
            if (this.registeredCommands.containsKey(alias)) {
                throw new IllegalArgumentException("Cannot register alias \"" + alias + "\" as it's been registered by another command already.");
            }
            this.registeredCommands.put(alias, command);
        });
    }

    public @NotNull CommandParseResult<T> processCommand(@NotNull final T commandSender, @NotNull final String alias, @NotNull final String input) {
        final Command<T> command = this.registeredCommands.get(alias.toLowerCase());

        if (command == null) {
            final CommandParseException exception = new CommandNotFoundException(null, alias.toLowerCase());

            return CommandParseResult.failure(
                () -> this.defaultExceptionHandler.handle(new CommandContext<>(commandSender, command), exception),
                exception
            );
        }
        return this.processCommand(commandSender, command, input);
    }

    public @NotNull CommandParseResult<T> processCommand(@NotNull final T commandSender, @NotNull final Command<T> command, @NotNull final String input) {
        final StringTraverser traverser = new StringTraverser(input.trim());
        final CommandNode<?, T> rootNode = command.getRootNode();
        final CommandContext<T> commandContext = new CommandContext<>(commandSender, command);
        final NodeParseResult<T> parseResult = this.processNode(command, commandContext, rootNode, new StringTraverser(traverser));

        return parseResult.parseResult;
    }

    private @NotNull NodeParseResult<T> processNode(@NotNull final Command<T> command, @NotNull final CommandContext<T> context, @NotNull final CommandNode<?, T> node, @NotNull final StringTraverser traverser) {
        final Argument<?, ?, T, ?> argument = node.getArgument();
        final ArgumentParser<?, T> argumentParser = argument.getParser();

        try {
            final CommandContext<T> localContext = new CommandContext<>(context);

            if (!argumentParser.preParse(new CommandContext<>(context), new StringTraverser(traverser))) {
                final CommandParseException exception = new PreparseFailureException(this.findExecutableParent(context.getSender(), command, node), argument.getId(), traverser.getContent());

                return new NodeParseResult<>(
                    CommandParseResult.failure(
                        () -> Optional.ofNullable(command.getCommandMetadata().getExceptionHandler())
                            .orElse(this.defaultExceptionHandler)
                            .handle(localContext, exception),
                        exception
                    ),
                    argument.getPriority()
                );
            }
            final ArgumentParseResult<?> parseResult = argumentParser.parse(localContext, traverser, false);

            if (!parseResult.getResult().isSuccess()) {
                throw parseResult.getException().get();
            }
            parseResult.getValue().ifPresent(value -> localContext.supply(argument.getId(), value));

            if (!traverser.hasNext()) {
                final Consumer<CommandContext<T>> handler = command.getNodeExecutionHandlers().get(node.getId());

                if (handler != null) {
                    final String permission = this.getPermissionForNode(command, node);

                    if (permission != null && !this.hasPermission(context.getSender(), permission)) {
                        final CommandParseException exception = new InsufficientPermissionsException(this.findExecutableParent(context.getSender(), command, node), permission);

                        return new NodeParseResult<>(
                            CommandParseResult.failure(
                                () -> Optional.ofNullable(command.getCommandMetadata().getExceptionHandler())
                                    .orElse(this.defaultExceptionHandler)
                                    .handle(localContext, exception),
                                exception
                            ),
                            Integer.MAX_VALUE
                        );
                    }
                    return new NodeParseResult<>(
                        CommandParseResult.success(() -> {
                            final Executor executor = Optional.ofNullable(command.getNodeExecutors().get(node.getId()))
                                .orElseGet(() -> Optional.ofNullable(command.getCommandMetadata().getDefaultExecutor())
                                    .orElseGet(() -> this.defaultExecutor)
                                );

                            if (executor == null) {
                                handler.accept(localContext);
                            } else {
                                executor.execute(() -> handler.accept(localContext));
                            }
                        }),
                        argument.getPriority()
                    );
                }
                final CommandParseException exception = new NoRegisteredActionException(this.findExecutableParent(context.getSender(), command, node), traverser.getContent());

                return new NodeParseResult<>(
                    CommandParseResult.failure(
                        () -> Optional.ofNullable(command.getCommandMetadata().getExceptionHandler())
                            .orElse(this.defaultExceptionHandler)
                            .handle(localContext, exception),
                        exception
                    ),
                    argument.getPriority()
                );
            }
            final List<CommandNode<?, T>> children = node.getChildren();
            NodeParseResult<T> bestResult = null;
            NodeParseResult<T> bestFailure = null;

            for (final CommandNode<?, T> child : children) {
                final StringTraverser childTraverser = new StringTraverser(traverser);
                childTraverser.traverseWhitespace();

                final NodeParseResult<T> childResult = this.processNode(command, localContext, child, childTraverser);

                if (childResult.parseResult().getResult() == CommandParseResult.ParseResult.SUCCESS) {
                    if (bestResult == null || childResult.priority() > bestResult.priority()) {
                        bestResult = childResult;
                    }
                } else if (bestFailure == null || childResult.priority() > bestFailure.priority()) {
                    bestFailure = childResult;
                }
            }
            if (bestResult != null) {
                return bestResult;
            }
            final CommandParseException exception = Optional.ofNullable(bestFailure)
                .flatMap(failure -> failure.parseResult.getException())
                .orElseGet(() -> new CommandParseException(this.findExecutableParent(context.getSender(), command, node), "Processed invalid nodes but found no errors."));

            return new NodeParseResult<>(
                CommandParseResult.failure(
                    () -> Optional.ofNullable(command.getCommandMetadata().getExceptionHandler())
                        .orElse(this.defaultExceptionHandler)
                        .handle(localContext, exception),
                    exception
                ),
                argument.getPriority()
            );
        } catch (final ArgumentParseException e) {
            final CommandParseException exception = new CommandParseException(this.findExecutableParent(context.getSender(), command, node), e.getMessage());

            return new NodeParseResult<>(
                CommandParseResult.failure(
                    () -> Optional.ofNullable(command.getCommandMetadata().getExceptionHandler())
                        .orElse(this.defaultExceptionHandler)
                        .handle(context, exception),
                    exception
                ),
                argument.getPriority()
            );
        }
    }

    public @NotNull List<Suggestion> processSuggestions(@NotNull final T commandSender, @NotNull final String alias, @NotNull final String input) {
        final Command<T> command = this.registeredCommands.get(alias.toLowerCase());

        if (command == null) {
            return Collections.emptyList();
        }
        return this.processSuggestions(commandSender, command, input);
    }

    public @NotNull List<Suggestion> processSuggestions(@NotNull final T commandSender, @NotNull final Command<T> command, @NotNull final String input) {
        final StringTraverser traverser = new StringTraverser(input);
        final CommandNode<?, T> rootNode = command.getRootNode();
        final CommandContext<T> commandContext = new CommandContext<>(commandSender, command);

        System.out.println("SUGGESTION INPUT = " + input);

        return this.processSuggestionsForNode(command, commandContext, rootNode, traverser)
            .stream()
            .distinct()
            .toList();
    }

    private @NotNull List<Suggestion> processSuggestionsForNode(@NotNull final Command<T> command, @NotNull final CommandContext<T> context, @NotNull final CommandNode<?, T> node, @NotNull final StringTraverser traverser) {
        final StringTraverser originalTraverser = new StringTraverser(traverser);
        final Argument<?, ?, T, ?> argument = node.getArgument();
        final ArgumentParser<?, T> argumentParser = argument.getParser();

        try {
            if (!argumentParser.isOrCouldBeValid(new CommandContext<>(context), new StringTraverser(traverser))) {
                return Collections.emptyList();
            }
            final CommandContext<T> localContext = new CommandContext<>(context);
            final StringTraverser localTraverser = new StringTraverser(traverser);
            final ArgumentParseResult<?> parseResult = argumentParser.parse(localContext, localTraverser, true);

            if (!parseResult.getResult().isSuccess()) {
                localTraverser.apply(originalTraverser);

                if (parseResult.getResult() == ArgumentParseResult.ParseResult.PROGRESS_SUGGESTIONS) {
                    return argumentParser.getSuggestionProvider().getSuggestions(new CommandContext<>(context), new StringTraverser(traverser));
                }
                throw parseResult.getException().get();
            }
            parseResult.getValue().ifPresent(value -> localContext.supply(argument.getId(), value));

            if (!localTraverser.hasNext()) {
                return argumentParser.getSuggestionProvider().getSuggestions(context, originalTraverser);
            }
            final List<CommandNode<?, T>> children = node.getChildren();
            final List<Suggestion> suggestions = new ArrayList<>();

            for (final CommandNode<?, T> child : children) {
                final String permission = this.getPermissionForNode(command, child);

                if (permission != null && !this.hasPermission(context.getSender(), permission)) {
                    continue;
                }
                final StringTraverser childTraverser = new StringTraverser(localTraverser);
                childTraverser.traverseWhitespace();

                suggestions.addAll(this.processSuggestionsForNode(command, new CommandContext<>(localContext), child, childTraverser));
            }
            return suggestions;
        } catch (final ArgumentParseException e) {
            if (originalTraverser.hasNext()) {
                return Collections.emptyList();
            }
            return argumentParser.getSuggestionProvider().getSuggestions(context, new StringTraverser(traverser));
        }
    }

    protected boolean hasPermission(@NotNull final T commandSender, @Nullable final String permission) {
        return true;
    }

    private @Nullable CommandNode<?, T> findExecutableParent(@NotNull final T commandSender, @NotNull final Command<T> command, @NotNull final CommandNode<?, T> node) {
        CommandNode<?, T> parent = node;

        while (parent != null) {
            if (command.getNodeExecutionHandlers().containsKey(parent.getId()) && this.hasPermission(commandSender, this.getPermissionForNode(command, parent))) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private @Nullable String getPermissionForNode(@NotNull final Command<T> command, @NotNull final CommandNode<?, T> node) {
        return Optional.ofNullable(command.getNodePermissions().get(node.getId()))
            .orElseGet(command.getCommandMetadata()::getPermission);
    }

    public final void printCommandTree() {
        this.registeredCommands
            .values()
            .stream()
            .distinct()
            .forEach(command -> this.printNode(command.getRootNode(), ""));
    }

    private void printNode(@NotNull final CommandNode<?, T> node, @NotNull final String indent) {
        if (node.getArgument() != null) {
            System.out.println(indent + node.getArgument().getId());
        }
        for (final CommandNode<?, T> child : node.getChildren()) {
            this.printNode(child, indent + "  ");
        }
    }

    private record NodeParseResult<T>(@NotNull CommandParseResult<T> parseResult, int priority) {

    }
}
