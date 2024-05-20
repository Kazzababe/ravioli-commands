package ravioli.gravioli.command.brigadier.context;

import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class BrigadierCommandContext<T, K> extends CommandContext<T> {
    private final com.mojang.brigadier.context.CommandContext<K> context;

    public BrigadierCommandContext(@NotNull final T contextSource, @NotNull final com.mojang.brigadier.context.CommandContext<K> context) {
        super(contextSource);

        this.context = context;
    }

    @Override
    public <B> @NotNull B get(final @NotNull String key) {
        return Objects.requireNonNull((B) this.context.getArgument(key, Object.class));
    }

    @Override
    public @NotNull <B> Optional<B> getOptional(final @NotNull String key) {
        return Optional.ofNullable(this.context.getArgument(key, Object.class))
            .map(value -> (B) value);
    }
}
