package ravioli.gravioli.command.paper.net;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.paper.PaperCommandManager;
import ravioli.gravioli.command.paper.event.AsyncRavioliTabCompleteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public final class PlayerPacketInterceptor extends ChannelDuplexHandler {
    private final PaperCommandManager paperCommandManager;
    private final Player player;

    @Override
    public void channelRead(final @NotNull ChannelHandlerContext ctx, final @NotNull Object msg) throws Exception {
        if (!(msg instanceof final ServerboundCommandSuggestionPacket packet)) {
            super.channelRead(ctx, msg);

            return;
        }
        final String buffer = packet.getCommand();

        if (buffer.isEmpty()) {
            super.channelRead(ctx, msg);

            return;
        }
        final String unprefixedBuffer = buffer.substring(1);
        final String alias = unprefixedBuffer.split(" ")[0];
        final Command<CommandSender> command = this.paperCommandManager.findCommand(alias);

        if (command == null) {
            super.channelRead(ctx, msg);

            return;
        }
        CompletableFuture.runAsync(() -> {
            final AsyncRavioliTabCompleteEvent event = new AsyncRavioliTabCompleteEvent(this.player, buffer, command, new ArrayList<>());

            event.callEvent();

            final List<Suggestion> suggestions = event.getSuggestions();

            if (suggestions.isEmpty()) {
                return;
            }
            final ClientboundCommandSuggestionsPacket outboundPacket = new ClientboundCommandSuggestionsPacket(
                packet.getId(),
                convertToSuggestions(suggestions, buffer)
            );

            ((CraftPlayer) this.player).getHandle().connection.send(outboundPacket);
        });
    }

    public static Suggestions convertToSuggestions(final List<Suggestion> suggestions, final String buffer) {
        final List<com.mojang.brigadier.suggestion.Suggestion> brigadierSuggestions = new ArrayList<>();

        for (final Suggestion suggestion : suggestions) {
            final String replace = suggestion.replace();
            final String text = suggestion.text();
            final String tooltip = suggestion.tooltip();

            int start = buffer.length() - replace.length();
            final int end = buffer.length();

            if (start < 0) {
                start = 0;
            }

            final StringRange range = new StringRange(start, end);
            final com.mojang.brigadier.suggestion.Suggestion brigadierSuggestion = new com.mojang.brigadier.suggestion.Suggestion(range, text, null);
            brigadierSuggestions.add(brigadierSuggestion);
        }

        int suggestionsStart = buffer.length();
        int suggestionsEnd = buffer.length();

        if (!brigadierSuggestions.isEmpty()) {
            suggestionsStart = brigadierSuggestions.get(0).getRange().getStart();
            suggestionsEnd = brigadierSuggestions.get(0).getRange().getEnd();
        }

        final StringRange suggestionsRange = new StringRange(suggestionsStart, suggestionsEnd);
        return new Suggestions(suggestionsRange, brigadierSuggestions);
    }
}
