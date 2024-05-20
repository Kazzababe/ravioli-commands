package ravioli.gravioli.command;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import ravioli.gravioli.command.argument.CommandTrack;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.command.RootCommandArgument;
import ravioli.gravioli.command.argument.execution.CommandExecutor;
import ravioli.gravioli.command.metadata.CommandMetadata;
import ravioli.gravioli.command.node.CommandNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public abstract class Command<T> {
    @Getter
    private final CommandNode<T, ?> rootNode;
    @Getter
    private final CommandMetadata<T> commandMetadata;
    private final Map<UUID, CommandExecutor<T>> nodeExecutionHandlers;
    private final Map<UUID, String> nodePermissions;
    private final Map<UUID, Executor> nodeExecutors;
    private final Set<String> allAliases;

    public Command() {
        final CommandMetadata<T> commandMetadata = this.createMetadata().build();
        final RootCommandArgument<T> rootArgument = new RootCommandArgument<>(commandMetadata);

        this.rootNode = new CommandNode<>(rootArgument);
        this.commandMetadata = commandMetadata;
        this.nodeExecutionHandlers = new HashMap<>();
        this.nodePermissions = new HashMap<>();
        this.nodeExecutors = new HashMap<>();

        final Set<String> allAliases = new HashSet<>();
        final String[] aliases = commandMetadata.getAliases();

        allAliases.add(commandMetadata.getName().toLowerCase());

        for (final String alias : aliases) {
            allAliases.add(alias.toLowerCase());
        }
        this.allAliases = Set.copyOf(allAliases);
    }

    @UnmodifiableView
    public final @NotNull Set<String> getAllAliases() {
        return this.allAliases;
    }

    @Contract("null -> null")
    public @Nullable String getNodePermission(@Nullable final CommandNode<T, ?> node) {
        if (node == null) {
            return null;
        }
        final CommandNode<T, ?> executableNode = node.getExecutableNode();

        if (executableNode == null) {
            return null;
        }
        return this.nodePermissions.get(node.getId());
    }

    @Contract("null -> false")
    public boolean canExecute(@Nullable final CommandNode<T, ?> node) {
        if (node == null) {
            return false;
        }
        final CommandNode<T, ?> executableNode = node.getExecutableNode();

        if (executableNode == null) {
            return false;
        }
        return this.nodeExecutionHandlers.containsKey(executableNode.getId());
    }

    @Contract("null -> null")
    public @Nullable CommandExecutor<T> getCommandExecutor(@Nullable final CommandNode<T, ?> node) {
        if (node == null) {
            return null;
        }
        final CommandNode<T, ?> executableNode = node.getExecutableNode();

        if (executableNode == null) {
            return null;
        }
        return this.nodeExecutionHandlers.get(executableNode.getId());
    }

    protected void add(@NotNull final CommandTrack<T> commandTrack) {
        this.addNode(commandTrack, this.rootNode, new ArrayList<>());
    }

    protected void add(@NotNull final CommandTrack.Builder<T> commandTrackBuilder) {
        this.addNode(commandTrackBuilder.build(), this.rootNode, new ArrayList<>());
    }

    private void addNode(
        @NotNull final CommandTrack<T> commandTrack,
        @NotNull final CommandNode<T, ?> currentNode,
        @NotNull final List<CommandNode<T, ?>> processedNodes
    ) {
        final CommandNode<T, ?> nextNode = commandTrack.getNodes().poll();

        processedNodes.add(currentNode);

        if (nextNode == null) {
            final CommandNode<T, ?> representativeNode = currentNode.getExecutableNode();

            if (representativeNode == null) {
                throw new IllegalArgumentException("Cannot register a command that consists of entirely optional arguments.");
            }
            Optional.ofNullable(commandTrack.getHandler()).ifPresent(handler -> {
                this.nodeExecutionHandlers.put(representativeNode.getId(), handler);
            });
            Optional.ofNullable(commandTrack.getPermission()).ifPresent(permission -> {
                this.nodePermissions.put(representativeNode.getId(), permission);

                processedNodes.forEach(node -> node.addChildPermission(permission));
            });
            Optional.ofNullable(commandTrack.getExecutor()).ifPresent(executor -> {
                this.nodeExecutors.put(representativeNode.getId(), executor);
            });

            return;
        }
        final CommandArgument<T, ?> nextArgument = nextNode.getArgument();

        for (final CommandNode<T, ?> child : currentNode.getChildren()) {
            final CommandArgument<T, ?> childArgument = child.getArgument();

            if (childArgument.getId().equals(nextArgument.getId())) {
                this.addNode(commandTrack, child, processedNodes);

                return;
            }
        }
        currentNode.addChild(nextNode);

        this.addNode(commandTrack, nextNode, processedNodes);
    }

    public abstract @NotNull CommandMetadata.Builder<T, ?> createMetadata();
}
