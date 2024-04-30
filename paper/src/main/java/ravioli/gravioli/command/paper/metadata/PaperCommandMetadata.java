package ravioli.gravioli.command.paper.metadata;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.exception.CommandExceptionHandler;
import ravioli.gravioli.command.metadata.CommandMetadata;

import java.util.concurrent.Executor;

@Getter
public final class PaperCommandMetadata extends CommandMetadata<CommandSender> {
    public static @NotNull Builder builder(@NotNull final String name) {
        return new Builder(name);
    }

    private final boolean overwriteCommands;

    private PaperCommandMetadata(
        final @NotNull String name,
        final @NotNull String[] aliases,
        final @Nullable String description,
        final @NotNull Executor defaultExecutor,
        final @Nullable String permission,
        final boolean overwriteCommands,
        final @Nullable CommandExceptionHandler<CommandSender> exceptionHandler
    ) {
        super(name, aliases, description, defaultExecutor, permission, exceptionHandler);

        this.overwriteCommands = overwriteCommands;
    }

    public static final class Builder extends CommandMetadata.Builder<CommandSender, Builder> {
        private boolean overwriteCommands;

        private Builder(final @NotNull String name) {
            super(name);
        }

        public @NotNull Builder overwriteCommands(final boolean overwriteCommands) {
            this.overwriteCommands = overwriteCommands;

            return this;
        }

        @Override
        public @NotNull PaperCommandMetadata build() {
            return new PaperCommandMetadata(
                this.name,
                this.aliases,
                this.description,
                this.defaultExecutor,
                this.permission,
                this.overwriteCommands,
                this.commandExceptionHandler
            );
        }
    }
}
