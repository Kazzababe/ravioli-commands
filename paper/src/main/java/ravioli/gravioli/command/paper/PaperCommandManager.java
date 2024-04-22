package ravioli.gravioli.command.paper;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.CommandManager;
import ravioli.gravioli.command.CommandNode;
import ravioli.gravioli.command.exception.permission.InsufficientPermissionsException;

public final class PaperCommandManager extends CommandManager<CommandSender> {
    private final Plugin plugin;

    private boolean registeredListener;

    public PaperCommandManager(@NotNull final Plugin plugin) {
        this.plugin = plugin;
        this.setDefaultExceptionHandler((commandContext, exception) -> {
            if (exception instanceof InsufficientPermissionsException) {
                commandContext.getSender().sendMessage(
                    MiniMessage.miniMessage().deserialize("<red>You don't have the required permissions for that.")
                );

                return;
            }
            final CommandNode<?, ?> node = exception.getNode();

            if (node == null) {
                commandContext.getSender().sendMessage(
                    MiniMessage.miniMessage().deserialize("<red>Error executing command: <gray>" + exception.getMessage())
                );

                return;
            }
            commandContext.getSender().sendMessage(
                MiniMessage.miniMessage().deserialize("<red>Invalid command syntax: <gray>/" + node.getUsageString())
            );
        });
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
        final String commandName = command.getCommandMetadata().getName().toLowerCase();

        if (commandMap.getKnownCommands().containsKey(commandName)) {
            return;
        }
        commandMap.register(commandName, new PaperCommandWrapper(this, command));
    }

    @Override
    protected boolean hasPermission(final @NotNull CommandSender commandSender, @Nullable final String permission) {
        if (permission == null) {
            return true;
        }
        return commandSender.hasPermission(permission);
    }

    public void enableAsynchronousCommandExecution() {
        this.setDefaultExecutor(task -> Bukkit.getScheduler().runTaskAsynchronously(this.plugin, task));
    }
}
