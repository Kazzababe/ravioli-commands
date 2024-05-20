package ravioli.gravioli.command.paper.brigadier;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.CommandManager;
import ravioli.gravioli.command.brigadier.BrigadierParser;
import ravioli.gravioli.command.paper.argument.location.LocationArgument;

public final class PaperBrigadierParser extends BrigadierParser<CommandSender, BukkitBrigadierCommandSource> {
    public PaperBrigadierParser(final @NotNull CommandManager<CommandSender> commandManager) {
        super(commandManager);

        this.registerMapping(
            LocationArgument.class,
            locationArg -> Vec3Argument.vec3()
        );
    }

    @Override
    protected @NotNull CommandSender getContextSource(@NotNull final BukkitBrigadierCommandSource context) {
        return context.getBukkitSender();
    }
}
