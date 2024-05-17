package ravioli.gravioli.command.paper.brigadier;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.context.CommandContext;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class BrigadierCommandContext extends CommandContext<CommandSender> {
    private final com.mojang.brigadier.context.CommandContext<BukkitBrigadierCommandSource> context;

    public BrigadierCommandContext(@NotNull final com.mojang.brigadier.context.CommandContext<BukkitBrigadierCommandSource> context) {
        super(context.getSource().getBukkitSender());

        this.context = context;
    }

    @Override
    public <K> @NotNull K get(final @NotNull String key) {
        return Objects.requireNonNull((K) this.context.getArgument(key, Object.class));
    }

    @Override
    public @NotNull <K> Optional<K> getOptional(final @NotNull String key) {
        return Optional.ofNullable(this.context.getArgument(key, Object.class))
            .map(value -> (K) value);
    }
}
