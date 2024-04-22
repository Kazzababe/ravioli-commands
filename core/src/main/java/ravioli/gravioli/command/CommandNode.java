package ravioli.gravioli.command;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.Argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public final class CommandNode<T, K> {
    private final UUID id;
    private final Argument<T, ?, K, ?> argument;
    private final List<CommandNode<?, K>> children;

    private CommandNode<?, K> parent;

    public CommandNode(@NotNull final Argument<T, ?, K, ?> argument) {
        this.id = UUID.randomUUID();
        this.argument = argument;
        this.children = new ArrayList<>();
    }

    public @NotNull List<CommandNode<?, K>> getChildren() {
        return List.copyOf(this.children);
    }

    public void addChild(@NotNull final CommandNode<?, K> node) {
        node.parent = this;

        this.children.add(node);
    }

    public @NotNull String getUsageString() {
        final List<String> parts = new ArrayList<>();
        CommandNode<?, K> node = this;

        while (node != null) {
            if (node.getArgument() != null) {
                parts.add(node.getArgument().getUsageRepresentation());
            }
            node = node.getParent();
        }
        Collections.reverse(parts);

        return String.join(" ", parts);
    }
}
