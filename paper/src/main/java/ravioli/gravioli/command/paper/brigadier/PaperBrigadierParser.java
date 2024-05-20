package ravioli.gravioli.command.paper.brigadier;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.CommandManager;
import ravioli.gravioli.command.brigadier.BrigadierParser;

public final class PaperBrigadierParser extends BrigadierParser<CommandSender, BukkitBrigadierCommandSource> {
    public PaperBrigadierParser(final @NotNull CommandManager<CommandSender> commandManager) {
        super(commandManager);
    }

    @Override
    protected @NotNull CommandSender getContextSource(@NotNull final BukkitBrigadierCommandSource context) {
        return context.getBukkitSender();
    }
}
