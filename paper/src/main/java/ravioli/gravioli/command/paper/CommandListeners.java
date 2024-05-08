package ravioli.gravioli.command.paper;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.argument.suggestion.Suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommandListeners implements Listener {
    private static final String PACKET_MD = "ravioli-command-applied";

    private final PaperCommandManager commandManager;
    private final MetadataValue packetMetadataValue;

    public CommandListeners(@NotNull final PaperCommandManager commandManager) {
        this.commandManager = commandManager;
        this.packetMetadataValue = new FixedMetadataValue(this.commandManager.getPlugin(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPreProcessTabCompletion(@NotNull final AsyncTabCompleteEvent event) {
        final String buffer = event.getBuffer().substring(1);
        final String[] args = buffer.split(" ");
        final Command<CommandSender> command = this.commandManager.findCommand(args[0]);

        if (command == null) {
            return;
        }
        final List<Suggestion> suggestions = this.commandManager.processSuggestions(event.getSender(), command, buffer);

        if (suggestions.isEmpty()) {
            return;
        }
        event.completions().addAll(
            suggestions.stream()
                .map(suggestion -> AsyncTabCompleteEvent.Completion.completion(
                    suggestion.text(),
                    MiniMessage.miniMessage().deserialize(suggestion.tooltip())
                ))
                .toList()
        );
    }
}
