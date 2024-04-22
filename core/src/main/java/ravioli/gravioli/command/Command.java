package ravioli.gravioli.command;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import ravioli.gravioli.command.argument.Argument;
import ravioli.gravioli.command.argument.RootArgument;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.metadata.CommandMetadata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Getter
public abstract class Command<T> {
    private final CommandNode<?, T> rootNode;
    private final CommandMetadata<T> commandMetadata;
    private final Map<UUID, Consumer<CommandContext<T>>> nodeExecutionHandlers;
    private final Map<UUID, String> nodePermissions;
    private final Map<UUID, Executor> nodeExecutors;
    private final Set<String> allAliases;

    public Command() {
        final CommandMetadata<T> commandMetadata = this.createMetadata().build();
        final RootArgument<T> rootArgument = new RootArgument<>(commandMetadata);

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

    protected void add(@NotNull final CommandTrack<T> commandTrack) {
        this.addNode(commandTrack, this.rootNode);
    }

    protected void add(@NotNull final CommandTrack.Builder<T> commandTrackBuilder) {
        this.addNode(commandTrackBuilder.build(), this.rootNode);
    }

    private void addNode(@NotNull final CommandTrack<T> commandTrack, @NotNull final CommandNode<?, T> currentNode) {
        final CommandNode<?, T> nextNode = commandTrack.getNodes().poll();

        if (nextNode == null) {
            Optional.ofNullable(commandTrack.getHandler()).ifPresent(handler -> {
                this.nodeExecutionHandlers.put(currentNode.getId(), handler);
            });
            Optional.ofNullable(commandTrack.getPermission()).ifPresent(permission -> {
                this.nodePermissions.put(currentNode.getId(), permission);
            });
            Optional.ofNullable(commandTrack.getExecutor()).ifPresent(executor -> {
                this.nodeExecutors.put(currentNode.getId(), executor);
            });

            return;
        }
        final Argument<?, ?, T, ?> nextArgument = nextNode.getArgument();

        for (final CommandNode<?, T> child : currentNode.getChildren()) {
            final Argument<?, ?, T, ?> childArgument = child.getArgument();

            if (childArgument.getId().equals(nextArgument.getId())) {
                this.addNode(commandTrack, child);

                return;
            }
        }
        currentNode.addChild(nextNode);

        this.addNode(commandTrack, nextNode);
    }

    public abstract @NotNull CommandMetadata.Builder<T, ?> createMetadata();
}
