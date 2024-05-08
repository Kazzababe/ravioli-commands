package ravioli.gravioli.command.paper;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.primitives.Ints;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.adventure.PaperAdventure;
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

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CommandListeners implements Listener {
    private static final String PACKET_MD = "ravioli-command-applied";

    private final PaperCommandManager commandManager;
    private final MetadataValue packetMetadataValue;

    public CommandListeners(@NotNull final PaperCommandManager commandManager) {
        this.commandManager = commandManager;
        this.packetMetadataValue = new FixedMetadataValue(this.commandManager.getPlugin(), true);
    }

    @EventHandler
    private void onSendTabCompletions(@NotNull final AsyncPlayerSendSuggestionsEvent event) {
        final String buffer = event.getBuffer();
        final Suggestions brigadierSuggestions = event.getSuggestions();
        final List<com.mojang.brigadier.suggestion.Suggestion> individualBrigadierSuggestions = new ArrayList<>();
        final SuggestionsBuilder builder0 = new com.mojang.brigadier.suggestion.SuggestionsBuilder(buffer, buffer.length());

        for (final com.mojang.brigadier.suggestion.Suggestion completion : brigadierSuggestions.getList()) {
            final String text = completion.getText();

            if (!text.contains(Suggestion.PRE_PROCESS_PREFIX)) {
                individualBrigadierSuggestions.add(completion);

                return;
            }
            final String[] args = text.split(Suggestion.PRE_PROCESS_DELIMITER, -1);

            if (args.length != 4) {
                return;
            }
            final Suggestion parsedSuggestion = new Suggestion(args[1], args[2], args[3]);
            final SuggestionsBuilder builder = builder0.createOffset(builder0.getInput().length() - parsedSuggestion.replace().length());
            final Integer intSuggestion = Ints.tryParse(parsedSuggestion.text());

            if (intSuggestion != null) {
                builder.suggest(intSuggestion, PaperAdventure.asVanilla(
                    MiniMessage.miniMessage().deserialize(parsedSuggestion.tooltip())
                ));
            } else {
                builder.suggest(parsedSuggestion.text(), PaperAdventure.asVanilla(
                    MiniMessage.miniMessage().deserialize(parsedSuggestion.tooltip())
                ));
            }
            builder0.add(builder);
        }
        final Suggestions build = builder0.build();

        build.getList().addAll(individualBrigadierSuggestions);

        event.setSuggestions(build);
    }

    @EventHandler(priority = EventPriority.MONITOR)
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
                .map(suggestion -> AsyncTabCompleteEvent.Completion.completion(suggestion.toProcessableFormat()))
                .toList()
        );
    }

    private static Suggestions convertToSuggestions(final List<Suggestion> suggestions, final String buffer) {
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
            final com.mojang.brigadier.suggestion.Suggestion brigadierSuggestion = new com.mojang.brigadier.suggestion.Suggestion(
                range,
                text,
                PaperAdventure.asVanilla(
                    MiniMessage.miniMessage().deserialize(tooltip)
                )
            );

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
