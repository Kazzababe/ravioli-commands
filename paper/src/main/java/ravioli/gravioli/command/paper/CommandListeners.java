package ravioli.gravioli.command.paper;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.argument.suggestion.Suggestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommandListeners implements Listener {
    private final PaperCommandManager commandManager;
    private final Plugin plugin;

    @EventHandler
    private void onTabComplete(@NotNull final AsyncTabCompleteEvent event) {
        final String buffer = event.getBuffer();

        if (buffer.isEmpty()) {
            return;
        }
        final String unprefixedBuffer = buffer.substring(1);
        final String alias = unprefixedBuffer.split(" ")[0];
        final Command<CommandSender> command = this.findCommand(alias);

        if (command == null) {
            return;
        }
        final String formattedBuffer = command.getCommandMetadata().getName() + unprefixedBuffer.substring(alias.length());
        final List<Suggestion> suggestions = this.commandManager.processSuggestions(event.getSender(), command, formattedBuffer);

        if (suggestions.isEmpty()) {
            return;
        }
        final List<AsyncTabCompleteEvent.Completion> completions = new ArrayList<>(event.completions());

        suggestions.forEach(suggestion -> {
            completions.add(AsyncTabCompleteEvent.Completion.completion(
                suggestion.text(),
                MiniMessage.miniMessage().deserialize("<gray>" + suggestion.tooltip())
            ));
        });
        event.completions(completions);
    }

    private @Nullable Command<CommandSender> findCommand(@NotNull final String alias) {
        if (!alias.contains(":")) {
            return this.commandManager.getCommand(alias);
        }
        final String[] parts = alias.split(":");
        final String namespace = parts[0];

        if (!namespace.equalsIgnoreCase(this.plugin.getName())) {
            return null;
        }
        final String remaining = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

        return this.commandManager.getCommand(remaining);
    }
}
