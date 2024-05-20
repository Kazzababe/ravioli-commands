package ravioli.gravioli.command.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.brigadier.BrigadierCommandManager;
import ravioli.gravioli.command.brigadier.BrigadierParser;
import ravioli.gravioli.command.paper.brigadier.PaperBrigadierParser;
import ravioli.gravioli.command.paper.metadata.PaperCommandMetadata;

import java.util.Arrays;
import java.util.Locale;

public final class PaperCommandManager extends BrigadierCommandManager<CommandSender, BukkitBrigadierCommandSource> {
    @Getter
    private final Plugin plugin;

    private boolean registeredListener;

    public PaperCommandManager(@NotNull final Plugin plugin) {
        this.plugin = plugin;
//        this.setDefaultExceptionHandler((commandContext, exception) -> {
//            if (exception instanceof InsufficientPermissionsException) {
//                commandContext.getSender().sendMessage(
//                    MiniMessage.miniMessage().deserialize("<red>You don't have the required permissions for that.")
//                );
//
//                return;
//            }
//            final CommandNode<?, ?> node = exception.getNode();
//
//            if (node == null) {
//                commandContext.getSender().sendMessage(
//                    MiniMessage.miniMessage().deserialize("<red>Error executing command: <gray>" + exception.getMessage())
//                );
//
//                return;
//            }
//            commandContext.getSender().sendMessage(
//                MiniMessage.miniMessage().deserialize("<red>Invalid command syntax: <gray>/" + node.getUsageString())
//            );
//        });
    }

    @Override
    public boolean doesEnvironmentSupportBrigadier() {
        try {
            Class.forName("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent");

            return super.doesEnvironmentSupportBrigadier();
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void registerCommand(final @NotNull Command<CommandSender> command) {
        if (!this.plugin.isEnabled()) {
            throw new IllegalStateException("Can't register command while plugin is not enabled.");
        }
        super.registerCommand(command);

        if (!this.registeredListener) {
            this.registeredListener = true;

            Bukkit.getPluginManager().registerEvents(new CommandListeners(this), this.plugin);
        }
        final CommandMap commandMap = Bukkit.getCommandMap();
        final String commandName = this.formatCommandName(command.getCommandMetadata().getName());

        if (command.getCommandMetadata() instanceof final PaperCommandMetadata paperCommandMetadata && paperCommandMetadata.isOverwriteCommands()) {
            commandMap.getKnownCommands().remove(commandName);

            command.getAllAliases().forEach(alias -> commandMap.getKnownCommands().remove(alias));
        }
        commandMap.register(commandName, this.plugin.getName().toLowerCase(Locale.ROOT), new PaperCommandWrapper(this, command));
    }

    @Override
    public boolean hasPermission(final @NotNull CommandSender commandSender, @Nullable final String permission) {
        if (permission == null) {
            return true;
        }
        return commandSender.hasPermission(permission);
    }

    public @Nullable Command<CommandSender> findCommand(@NotNull final String alias) {
        if (!alias.contains(":")) {
            return this.getCommand(alias);
        }
        final String[] parts = alias.split(":");
        final String namespace = parts[0];

        if (!namespace.equalsIgnoreCase(this.plugin.getName())) {
            return null;
        }
        final String remaining = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

        return this.getCommand(remaining);
    }

    @Override
    protected BrigadierParser<CommandSender, BukkitBrigadierCommandSource> createParser() {
        return new PaperBrigadierParser(this);
    }
}
