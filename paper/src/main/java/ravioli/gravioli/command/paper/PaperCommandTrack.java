package ravioli.gravioli.command.paper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.CommandTrack;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.execution.CommandExecutor;
import ravioli.gravioli.command.node.CommandNode;

import java.util.Queue;
import java.util.concurrent.Executor;

public final class PaperCommandTrack extends CommandTrack<CommandSender> {

    public static @NotNull Builder command() {
        return new Builder();
    }

    private PaperCommandTrack(
        @NotNull final Queue<CommandNode<CommandSender, ?>> commandNodes,
        @NotNull final CommandExecutor<CommandSender> handler,
        @Nullable final String permission,
        @Nullable final Executor executor
    ) {
        super(commandNodes, handler, permission, executor);
    }

    @Override
    public Queue<CommandNode<CommandSender, ?>> getNodes() {
        return super.getNodes();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder extends CommandTrack.Builder<CommandSender>  {
        @Override
        public PaperCommandTrack.@NotNull Builder argument(@NotNull final CommandArgument<CommandSender, ?> argument) {
            return (PaperCommandTrack.Builder) super.argument(argument);
        }

        @Override
        public @NotNull PaperCommandTrack.Builder handler(@NotNull final CommandExecutor<CommandSender> handler) {
            return (PaperCommandTrack.Builder) super.handler(handler);
        }

        @Override
        public @NotNull PaperCommandTrack build() {
            return new PaperCommandTrack(this.nodes, this.handler, this.permission, this.executor);
        }
    }
}
