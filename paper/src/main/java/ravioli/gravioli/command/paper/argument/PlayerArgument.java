package ravioli.gravioli.command.paper.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.List;

public final class PlayerArgument extends CommandArgument<CommandSender, Player> {
    public static @NotNull PlayerArgumentBuilder of(@NotNull final String id) {
        return new PlayerArgumentBuilder(id);
    }
    public static @NotNull PlayerArgumentBuilder optional(@NotNull final String id) {
        return new PlayerArgumentBuilder(id)
            .optional(true);
    }

    private PlayerArgument(final @NotNull String id) {
        super(id);
    }

    @Override
    protected @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        return null;
    }

    @Override
    public @Nullable Player parse(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        return null;
    }

    @Override
    public @NotNull ArgumentType<?> getBrigadierType() {
        return StringArgumentType.word();
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        return false;
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        return false;
    }

    public static final class PlayerArgumentBuilder extends CommandArgumentBuilder<CommandSender, Player, PlayerArgument, PlayerArgumentBuilder> {
        private PlayerArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        @Override
        protected PlayerArgument createArgument() {
            return new PlayerArgument(this.id);
        }
    }
}
