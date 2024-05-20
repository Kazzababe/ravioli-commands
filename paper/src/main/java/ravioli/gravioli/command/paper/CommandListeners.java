package ravioli.gravioli.command.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.argument.suggestion.Suggestion;

import java.util.List;
import java.util.regex.Pattern;

public final class CommandListeners implements Listener {
    private final PaperCommandManager commandManager;

    public CommandListeners(@NotNull final PaperCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    private void onCommandRegistered(@NotNull final CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if (!this.commandManager.doesEnvironmentSupportBrigadier()) {
            return;
        }
        final var command = event.getCommand();

        if (!(command instanceof final PluginIdentifiableCommand pluginCommand)) {
            return;
        }
        if (!pluginCommand.getPlugin().equals(this.commandManager.getPlugin())) {
            return;
        }
        final String commandLabel = event.getCommandLabel();
        final String label;

        if (commandLabel.contains(":")) {
            label = commandLabel.split(Pattern.quote(":"))[1];
        } else {
            label = commandLabel;
        }
        final Command<CommandSender> ravioliCommand = this.commandManager.getCommand(label);

        if (ravioliCommand == null) {
            return;
        }
        event.setLiteral(this.commandManager.getBrigadierParser().createCommandNode(commandLabel, ravioliCommand));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPreProcessTabCompletion(@NotNull final AsyncTabCompleteEvent event) {
        if (this.commandManager.doesEnvironmentSupportBrigadier()) {
            return;
        }
        final String buffer = event.getBuffer().substring(1);
        final String[] args = buffer.split(" ");
        final Command<CommandSender> command = this.commandManager.findCommand(args[0]);

        if (command == null) {
            return;
        }
        final String newBuffer = command.getCommandMetadata().getName() + buffer.substring(args[0].length());
        final List<Suggestion> suggestions = this.commandManager.getSuggestions(event.getSender(), newBuffer);

        if (suggestions.isEmpty()) {
            return;
        }
        event.completions().addAll(
            suggestions.stream()
                .map(suggestion -> AsyncTabCompleteEvent.Completion.completion(
                    suggestion.text(),
                    Component.text(suggestion.tooltip())
                ))
                .toList()
        );
    }
}
