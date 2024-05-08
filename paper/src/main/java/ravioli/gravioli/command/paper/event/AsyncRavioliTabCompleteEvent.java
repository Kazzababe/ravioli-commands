package ravioli.gravioli.command.paper.event;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.argument.suggestion.Suggestion;

import java.util.Collection;
import java.util.List;

@Getter
public final class AsyncRavioliTabCompleteEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final String buffer;
    private final String processableBuffer;
    private final Command<CommandSender> command;
    private final List<Suggestion> suggestions;

    public AsyncRavioliTabCompleteEvent(@NotNull final Player who, @NotNull final String buffer, @NotNull final Command<CommandSender> command, @NotNull final List<Suggestion> suggestions) {
        super(who, true);

        this.buffer = buffer;
        this.command = command;
        this.suggestions = suggestions;

        final String unprefixedBuffer = buffer.substring(1);
        final String alias = unprefixedBuffer.split(" ")[0];

        this.processableBuffer = command.getCommandMetadata().getName() + unprefixedBuffer.substring(alias.length());
    }

    public void setSuggestions(@NotNull final List<Suggestion> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions.stream().distinct().toList());
    }

    public void addSuggestions(@NotNull final Collection<Suggestion> suggestions) {
        this.suggestions.addAll(suggestions.stream().distinct().toList());
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
