package ravioli.gravioli.command.paper.metadata;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.exception.CommandExceptionHandler;
import ravioli.gravioli.command.metadata.CommandMetadata;

import java.util.concurrent.Executor;

public final class PaperCommandMetadata extends CommandMetadata<CommandSender> {
    public static @NotNull Builder builder(@NotNull final String name) {
        return new Builder(name);
    }

    private PaperCommandMetadata(
        final @NotNull String name,
        final @NotNull String[] aliases,
        final @Nullable String description,
        final @NotNull Executor defaultExecutor,
        final @Nullable String permission,
        final @Nullable CommandExceptionHandler<CommandSender> exceptionHandler
    ) {
        super(name, aliases, description, defaultExecutor, permission, exceptionHandler);
    }

    public static final class Builder extends CommandMetadata.Builder<CommandSender, Builder> {
        private Builder(final @NotNull String name) {
            super(name);
        }
    }
}
