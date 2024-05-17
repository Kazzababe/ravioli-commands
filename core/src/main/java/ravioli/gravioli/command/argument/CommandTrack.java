package ravioli.gravioli.command.argument;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.execution.CommandExecutor;
import ravioli.gravioli.command.node.CommandNode;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommandTrack<T> {
    private final Queue<CommandNode<T, ?>> nodes;
    private final CommandExecutor<T> handler;
    private final String permission;
    private final Executor executor;

    public static class Builder<T> {
        protected final Queue<CommandNode<T, ?>> nodes = new LinkedList<>();

        protected CommandExecutor<T> handler;
        protected String permission;
        protected Executor executor;

        public @NotNull Builder<T> argument(@NotNull final CommandArgument<T, ?> argument) {
            this.nodes.add(new CommandNode<>(argument));

            return this;
        }

        public @NotNull Builder<T> argument(@NotNull final CommandArgument.CommandArgumentBuilder<T, ?, ?, ?> argument) {
            this.nodes.add(new CommandNode<>(argument.build()));

            return this;
        }

        public @NotNull Builder<T> handler(@NotNull final CommandExecutor<T> handler) {
            this.handler = handler;

            return this;
        }

        public @NotNull Builder<T> permission(@NotNull final String permission) {
            this.permission = permission;

            return this;
        }

        public @NotNull Builder<T> executor(@NotNull final Executor executor) {
            this.executor = executor;

            return this;
        }

        public @NotNull CommandTrack<T> build() {
            if (this.handler == null) {
                throw new IllegalStateException("Cannot create a command without a handler.");
            }
            return new CommandTrack<>(this.nodes, this.handler, this.permission, this.executor);
        }
    }
}
