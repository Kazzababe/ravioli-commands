package ravioli.gravioli.command.paper.argument.location;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.List;

public final class LocationArgument extends CommandArgument<CommandSender, CommandLocation> {
    public static @NotNull LocationArgument of(@NotNull final String id) {
        return new LocationArgument(id);
    }

    private static final String RELATIVE_PREFIX = "~";

    private LocationArgument(@NotNull final String id) {
        super(id);
    }

    @Override
    protected @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        return null;
    }

    @Override
    public @Nullable CommandLocation parse(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        return null;
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        return false;
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        return false;
    }

    @Override
    public ArgumentType<?> getBrigadierType() {
        return Vec3Argument.vec3();
    }
}
