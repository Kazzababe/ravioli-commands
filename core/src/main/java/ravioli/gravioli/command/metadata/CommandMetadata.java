package ravioli.gravioli.command.metadata;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.exception.CommandExceptionHandler;

import java.util.concurrent.Executor;

@Getter
public class CommandMetadata<T> {
    private final String name;
    private final String[] aliases;
    private final String description;
    private final Executor defaultExecutor;
    private final String permission;
    private final CommandExceptionHandler<T> exceptionHandler;

    protected CommandMetadata(
        @NotNull final String name,
        @NotNull final String[] aliases,
        @Nullable final String description,
        @NotNull final Executor defaultExecutor,
        @Nullable final String permission,
        @Nullable final CommandExceptionHandler<T> exceptionHandler
    ) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.defaultExecutor = defaultExecutor;
        this.permission = permission;
        this.exceptionHandler = exceptionHandler;
    }

    public static class Builder<T, K extends Builder<T, K>> {
        private final String name;

        private String[] aliases;
        private String description;
        private Executor defaultExecutor;
        private String permission;
        private CommandExceptionHandler<T> commandExceptionHandler;

        public Builder(@NotNull final String name) {
            this.name = name;
            this.aliases = new String[0];
        }

        public @NotNull K aliases(@NotNull final String firstAlias, @NotNull final String... otherAliases) {
            final String[] aliases = new String[otherAliases.length + 1];

            aliases[0] = firstAlias;
            System.arraycopy(otherAliases, 0, aliases, 1, otherAliases.length);

            this.aliases = aliases;

            return (K) this;
        }

        public @NotNull K description(@NotNull final String description) {
            this.description = description;

            return (K) this;
        }

        public @NotNull K defaultExecutor(@NotNull final Executor defaultExecutor) {
            this.defaultExecutor = defaultExecutor;

            return (K) this;
        }

        public @NotNull K permission(@NotNull final String permission) {
            this.permission = permission;

            return (K) this;
        }

        public @NotNull K exceptionHandler(@NotNull final CommandExceptionHandler<T> exceptionHandler) {
            this.commandExceptionHandler = exceptionHandler;

            return (K) this;
        }

        public @NotNull CommandMetadata<T> build() {
            return new CommandMetadata<>(this.name, this.aliases, this.description, this.defaultExecutor, this.permission, this.commandExceptionHandler);
        }
    }
}
