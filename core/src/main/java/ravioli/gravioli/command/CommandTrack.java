package ravioli.gravioli.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.Argument;
import ravioli.gravioli.command.context.CommandContext;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommandTrack<T> {
    private final Queue<CommandNode<?, T>> nodes;
    private final Consumer<CommandContext<T>> handler;
    private final String permission;
    private final Executor executor;

    public static class Builder<T> {
        protected final Queue<CommandNode<?, T>> nodes = new LinkedList<>();

        protected Consumer<CommandContext<T>> handler;
        protected String permission;
        protected Executor executor;

        public @NotNull Builder<T> argument(@NotNull final Argument<?, ?, T, ?> argument) {
            this.nodes.add(new CommandNode<>(argument));

            return this;
        }

        public @NotNull Builder<T> handler(@NotNull final Consumer<CommandContext<T>> handler) {
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
