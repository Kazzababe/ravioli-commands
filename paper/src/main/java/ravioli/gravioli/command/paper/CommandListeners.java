package ravioli.gravioli.command.paper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.paper.event.AsyncRavioliTabCompleteEvent;
import ravioli.gravioli.command.paper.net.PlayerPacketInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommandListeners implements Listener {
    private final PaperCommandManager commandManager;
    private final Map<UUID, PlayerPacketInterceptor> packetInterceptors = new HashMap<>();

    @EventHandler
    private void onTabComplete(@NotNull final AsyncRavioliTabCompleteEvent event) {
        final Command<CommandSender> command = event.getCommand();
        final String buffer = event.getProcessableBuffer();
        final List<Suggestion> suggestions = this.commandManager.processSuggestions(event.getPlayer(), command, buffer);

        if (suggestions.isEmpty()) {
            return;
        }
        final List<Suggestion> completions = new ArrayList<>(event.getSuggestions());

        completions.addAll(suggestions);
        event.setSuggestions(completions);
    }

    @EventHandler
    private void onPlayerJoin(@NotNull final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final PlayerPacketInterceptor packetInterceptor = new PlayerPacketInterceptor(this.commandManager, player);

        this.packetInterceptors.put(player.getUniqueId(), packetInterceptor);
        serverPlayer.connection.connection.channel.pipeline().addBefore("packet_handler", player.getName(), packetInterceptor);
    }

    @EventHandler
    private void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerPacketInterceptor packetInterceptor = this.packetInterceptors.remove(player.getUniqueId());

        if (packetInterceptor == null) {
            return;
        }
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        serverPlayer.connection.connection.channel.pipeline().remove(packetInterceptor);
    }
}
