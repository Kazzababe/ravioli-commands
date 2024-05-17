package ravioli.gravioli.command.node;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.argument.CommandProcessable;
import ravioli.gravioli.command.argument.command.CommandArgument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public final class CommandNode<T, K> implements CommandProcessable<T> {
    private final UUID id;
    private final CommandArgument<T, K> argument;
    private final List<CommandNode<T, ?>> children;
    private final Set<String> childrenPermissions;

    private CommandNode<T, ?> parent;

    public CommandNode(@NotNull final CommandArgument<T, K> argument) {
        this.id = UUID.randomUUID();
        this.argument = argument;
        this.children = new ArrayList<>();
        this.childrenPermissions = new HashSet<>();
    }

    public @Nullable CommandNode<T, ?> getExecutableNode() {
        CommandNode<T, ?> currentNode = this;

        while (currentNode != null && currentNode.getArgument().isOptional()) {
            currentNode = currentNode.getParent();
        }
        return currentNode;
    }

    public @NotNull List<CommandNode<T, ?>> getChildren() {
        return List.copyOf(this.children);
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public void addChild(@NotNull final CommandNode<T, ?> node) {
        node.parent = this;

        this.children.add(node);
    }

    public void addChildPermission(@NotNull final String permission) {
        this.childrenPermissions.add(permission);
    }

    public @NotNull String getUsageString() {
//        final List<String> parts = new ArrayList<>();
//        CommandNode<?, K> node = this;
//
//        while (node != null) {
//            if (node.getArgument() != null) {
//                parts.add(node.getArgument().getUsageRepresentation());
//            }
//            node = node.getParent();
//        }
//        Collections.reverse(parts);
//
//        return String.join(" ", parts);
        return "";
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        return this.argument.isValidForSuggestions(context, traverser);
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<T> context, @NotNull final StringTraverser traverser) {
        return this.argument.isValidForExecution(context, traverser);
    }
}
