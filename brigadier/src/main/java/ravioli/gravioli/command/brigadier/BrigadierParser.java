package ravioli.gravioli.command.brigadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.CommandManager;
import ravioli.gravioli.command.argument.CommandArgumentType;
import ravioli.gravioli.command.argument.command.BooleanArgument;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.command.DoubleArgument;
import ravioli.gravioli.command.argument.command.EnumArgument;
import ravioli.gravioli.command.argument.command.FloatArgument;
import ravioli.gravioli.command.argument.command.IntegerArgument;
import ravioli.gravioli.command.argument.command.LiteralArgument;
import ravioli.gravioli.command.argument.command.LongArgument;
import ravioli.gravioli.command.argument.command.StringArgument;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.brigadier.context.BrigadierCommandContext;
import ravioli.gravioli.command.brigadier.mapper.BrigadierMapper;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("rawtypes")
public abstract class BrigadierParser<T, K> {
    private static final BrigadierMapper DEFAULT_MAPPER = arg -> StringArgumentType.word();

    private final Map<Class<? extends CommandArgument<?, ?>>, BrigadierMapper<?>> brigadierMappings = new HashMap<>();

    private final CommandManager<T> commandManager;

    public BrigadierParser(@NotNull final CommandManager<T> commandManager) {
        this.commandManager = commandManager;
        this.registerDefaultMappings();
    }


    public final <A extends CommandArgument<?, ?>> void registerMapping(
        @NotNull final Class<A> argumentClass,
        @NotNull final BrigadierMapper<A> mapper
    ) {
        this.brigadierMappings.put(argumentClass, mapper);
    }

    private void registerDefaultMappings() {
        this.registerMapping(
            BooleanArgument.class,
            booleanArg -> BoolArgumentType.bool()
        );
        this.registerMapping(
            DoubleArgument.class,
            doubleArg -> {
                if (doubleArg.getMinimum() != null) {
                    if (doubleArg.getMaximum() != null) {
                        return DoubleArgumentType.doubleArg(doubleArg.getMinimum(), doubleArg.getMaximum());
                    }
                    return DoubleArgumentType.doubleArg(doubleArg.getMinimum());
                }
                return DoubleArgumentType.doubleArg();
            }
        );
        this.registerMapping(
            EnumArgument.class,
            enumArg -> StringArgumentType.word()
        );
        this.registerMapping(
            FloatArgument.class,
            floatArg -> {
                if (floatArg.getMinimum() != null) {
                    if (floatArg.getMaximum() != null) {
                        return FloatArgumentType.floatArg(floatArg.getMinimum(), floatArg.getMaximum());
                    }
                    return FloatArgumentType.floatArg(floatArg.getMinimum());
                }
                return FloatArgumentType.floatArg();
            }
        );
        this.registerMapping(
            IntegerArgument.class,
            integerArg -> {
                if (integerArg.getMinimum() != null) {
                    if (integerArg.getMaximum() != null) {
                        return IntegerArgumentType.integer(integerArg.getMinimum(), integerArg.getMaximum());
                    }
                    return IntegerArgumentType.integer(integerArg.getMinimum());
                }
                return IntegerArgumentType.integer();
            }
        );
        this.registerMapping(
            LiteralArgument.class,
            longArg -> StringArgumentType.word()
        );
        this.registerMapping(
            LongArgument.class,
            longArg -> {
                if (longArg.getMinimum() != null) {
                    if (longArg.getMaximum() != null) {
                        return LongArgumentType.longArg(longArg.getMinimum(), longArg.getMaximum());
                    }
                    return LongArgumentType.longArg(longArg.getMinimum());
                }
                return LongArgumentType.longArg();
            }
        );
        this.registerMapping(
            StringArgument.class,
            stringArg -> switch (stringArg.getStringMode()) {
                case GREEDY -> StringArgumentType.greedyString();
                case QUOTES -> StringArgumentType.string();
                case WORD -> StringArgumentType.word();
            }
        );
    }

    public final @NotNull LiteralCommandNode<K> createCommandNode(@NotNull final String alias, @NotNull final Command<T> command) {
        final ravioli.gravioli.command.node.CommandNode<T, ?> rootNode = command.getRootNode();
        final LiteralArgumentBuilder<K> literalArgumentBuilder = LiteralArgumentBuilder
            .literal(alias);
        final LiteralCommandNode<K> literalBuild = literalArgumentBuilder.build();

        rootNode.getChildren().forEach(child -> literalBuild.addChild(this.createBrigadierNode(command, child).build()));

        return literalBuild;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private @NotNull ArgumentBuilder<K, ?> createBrigadierNode(@NotNull final Command<T> command, @NotNull final ravioli.gravioli.command.node.CommandNode<T, ?> ravioliNode) {
        final CommandArgument<T, ?> argument = ravioliNode.getArgument();
        final ArgumentBuilder<K, ?> brigadierNode;

        if (argument.getType() == CommandArgumentType.LITERAL) {
            brigadierNode = LiteralArgumentBuilder.literal(
                ((LiteralArgument<T>) argument).getLowerCaseLabel()
            );
        } else {
            final BrigadierMapper brigadierMapper = this.brigadierMappings.getOrDefault(argument.getClass(), DEFAULT_MAPPER);
            final ArgumentType<Object> brigadierType = brigadierMapper.convert(argument);

            brigadierNode = RequiredArgumentBuilder.argument(argument.getId(), brigadierType);

            if (!argument.shouldDefaultSuggestionsToBrigadier() || argument.getSuggestionProvider() != null) {
                ((RequiredArgumentBuilder<K, Object>) brigadierNode)
                    .suggests((context, builder) -> {
                        final StringTraverser traverser = new StringTraverser(builder.getInput());
                        final CommandContext<T> commandContext = new BrigadierCommandContext(this.getContextSource(context.getSource()), context);
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
                                        new LiteralMessage(suggestion.tooltip())
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
                    final CommandContext<T> commandContext = new BrigadierCommandContext(this.getContextSource(context.getSource()), context);

                    Optional.ofNullable(command.getCommandExecutor(ravioliNode)).ifPresent(executor -> {
                        executor.execute(commandContext);
                    });

                    return 1;
                })
                .requires(source ->
                    this.commandManager.hasPermission(this.getContextSource(source), command.getNodePermission(ravioliNode))
                );
        } else {
            brigadierNode.requires(source -> {
                final T contextSource = this.getContextSource(source);

                return ravioliNode.getChildrenPermissions().isEmpty() ||
                    ravioliNode.getChildrenPermissions()
                        .stream()
                        .anyMatch(node -> this.commandManager.hasPermission(contextSource, node));
            });
        }
        ravioliNode.getChildren().forEach(child -> brigadierNode.then(this.createBrigadierNode(command, child)));

        return brigadierNode;
    }

    protected abstract @NotNull T getContextSource(@NotNull K context);
}
