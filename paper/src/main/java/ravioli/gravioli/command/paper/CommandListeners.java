package ravioli.gravioli.command.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.command.LiteralArgument;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.paper.brigadier.BrigadierCommandContext;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public final class CommandListeners implements Listener {
    private static final String PRE_PROCESS_PREFIX = "__ravioli_commands__";
    private static final String PRE_PROCESS_DELIMITER = "⌶⌫⌶";

    private final PaperCommandManager commandManager;

    public CommandListeners(@NotNull final PaperCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @EventHandler
    private void onSendTabCompletions(@NotNull final AsyncPlayerSendSuggestionsEvent event) {
        final String buffer = event.getBuffer();
        final Suggestions brigadierSuggestions = event.getSuggestions();
        final SuggestionsBuilder builder0 = new com.mojang.brigadier.suggestion.SuggestionsBuilder(buffer, buffer.length());
        final com.mojang.brigadier.suggestion.SuggestionsBuilder builder = builder0.createOffset(builder0.getInput().lastIndexOf(' ') + 1);

        for (final com.mojang.brigadier.suggestion.Suggestion completion : brigadierSuggestions.getList()) {
            final String text = completion.getText();

            if (!text.contains(PRE_PROCESS_PREFIX)) {
                return;
            }
            final String[] args = text.split(PRE_PROCESS_DELIMITER, -1);

            if (args.length != 4) {
                return;
            }
            final String rangeString = args[1];
            StringRange stringRange = null;

            if (rangeString.contains(":")) {
                final String[] ranges = rangeString.split(":");

                stringRange = new StringRange(
                    Integer.parseInt(ranges[0]),
                    Integer.parseInt(ranges[1])
                );
            }
            final Suggestion parsedSuggestion = new Suggestion(args[2], args[3], stringRange);

            builder.suggest(parsedSuggestion.text(), PaperAdventure.asVanilla(
                MiniMessage.miniMessage().deserialize(parsedSuggestion.tooltip())
            ));
        }
        event.setSuggestions(builder.build());
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    private void onCommandRegistered(@NotNull final CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if (!this.commandManager.isUseBrigadier()) {
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
        final ravioli.gravioli.command.node.CommandNode<CommandSender, ?> rootNode = ravioliCommand.getRootNode();
        final LiteralArgumentBuilder<BukkitBrigadierCommandSource> literalArgumentBuilder = LiteralArgumentBuilder
            .literal(commandLabel);
        final LiteralCommandNode<BukkitBrigadierCommandSource> literalBuild = literalArgumentBuilder.build();

        rootNode.getChildren().forEach(child -> literalBuild.addChild(this.createBrigadierNode(ravioliCommand, child).build()));

        event.setLiteral(literalBuild);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPreProcessTabCompletion(@NotNull final AsyncTabCompleteEvent event) {
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
                .map(suggestion -> AsyncTabCompleteEvent.Completion.completion(this.toProcessableFormat(suggestion)))
                .toList()
        );
    }

    private @NotNull String toProcessableFormat(@NotNull final Suggestion suggestion) {
        String rangeString = "";

        if (suggestion.range() != null) {
            rangeString = suggestion.range().getStart() + ":" + suggestion.range().getEnd();
        }
        return PRE_PROCESS_PREFIX + PRE_PROCESS_DELIMITER + rangeString + PRE_PROCESS_DELIMITER + suggestion.text() + PRE_PROCESS_DELIMITER + suggestion.tooltip();
    }

    @SuppressWarnings("unchecked")
    private @NotNull ArgumentBuilder<BukkitBrigadierCommandSource, ?> createBrigadierNode(@NotNull final Command<CommandSender> command, @NotNull final ravioli.gravioli.command.node.CommandNode<CommandSender, ?> ravioliNode) {
        final CommandArgument<CommandSender, ?> argument = ravioliNode.getArgument();
        final ArgumentBuilder<BukkitBrigadierCommandSource, ?> brigadierNode;

        if (argument instanceof final LiteralArgument literalArgument) {
            brigadierNode = LiteralArgumentBuilder.literal(literalArgument.getLowerCaseLabel());
        } else {
            final ArgumentType<?> brigadierType = argument.getBrigadierType();

            brigadierNode = RequiredArgumentBuilder.<BukkitBrigadierCommandSource, Object>argument(argument.getId(), (ArgumentType<Object>) brigadierType);

            if (!argument.shouldDefaultSuggestionsToBrigadier() || argument.getSuggestionProvider() != null) {
                ((RequiredArgumentBuilder<BukkitBrigadierCommandSource, Object>) brigadierNode)
                    .suggests((context, builder) -> {
                        if (context.getSource().getBukkitSender() == null) {
                            return Suggestions.empty();
                        }
                        final StringTraverser traverser = new StringTraverser(builder.getInput());
                        final CommandContext<CommandSender> commandContext = new BrigadierCommandContext(context);
                        final int startPosition = builder.getStart();
                        final int endPosition = builder.getInput().length();
                        final String remaining = builder.getRemainingLowerCase();

                        traverser.setCursor(builder.getInput().lastIndexOf(builder.getRemaining()));

                        return CompletableFuture.supplyAsync(() -> {
                            final List<Suggestion> suggestions = argument.getSuggestions(commandContext, new StringTraverser(traverser));
                            final List<com.mojang.brigadier.suggestion.Suggestion> finalSuggestions = new ArrayList<>();

                            for (final Suggestion suggestion : suggestions) {
                                final String text = suggestion.text();

                                if (text.equals(remaining)) {
                                    continue;
                                }
                                final StringRange stringRange = StringRange.between(startPosition, endPosition);

                                finalSuggestions.add(
                                    new com.mojang.brigadier.suggestion.Suggestion(
                                        stringRange,
                                        text,
                                        PaperAdventure.asVanilla(
                                            MiniMessage.miniMessage().deserialize(suggestion.tooltip())
                                        )
                                    )
                                );
                            }

                            return Suggestions.create(builder.getInput(), finalSuggestions);
                        });
                    });
            }
        }
        if (command.canExecute(ravioliNode)) {
            brigadierNode
                .executes(context -> {
                    final CommandContext<CommandSender> commandContext = new BrigadierCommandContext(context);

                    Optional.ofNullable(command.getCommandExecutor(ravioliNode)).ifPresent(executor -> {
                        executor.execute(commandContext);
                    });

                    return 1;
                })
                .requires(source ->
                    this.commandManager.hasPermission(source.getBukkitSender(), command.getNodePermission(ravioliNode))
                );
        } else {
            brigadierNode.requires(source ->
                ravioliNode.getChildrenPermissions().isEmpty() ||
                    ravioliNode.getChildrenPermissions()
                        .stream()
                        .anyMatch(node -> this.commandManager.hasPermission(source.getBukkitSender(), node))
            );
        }
        ravioliNode.getChildren().forEach(child -> brigadierNode.then(this.createBrigadierNode(command, child)));

        return brigadierNode;
    }
}
