package ravioli.gravioli.example;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.EnumArgument;
import ravioli.gravioli.command.argument.IntegerArgument;
import ravioli.gravioli.command.argument.LiteralArgument;
import ravioli.gravioli.command.argument.StringArgument;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.paper.PaperCommand;
import ravioli.gravioli.command.paper.PaperCommandTrack;
import ravioli.gravioli.command.paper.argument.location.CommandLocation;
import ravioli.gravioli.command.paper.argument.location.LocationArgument;
import ravioli.gravioli.command.paper.metadata.PaperCommandMetadata;

import java.util.Collections;

public final class ExampleCommand extends PaperCommand {
    public ExampleCommand(@NotNull final Plugin plugin) {
        this.add(
            PaperCommandTrack.command()
                .argument(EnumArgument.of("value", Material.class))
                .handler(context -> context.getSender().sendMessage("Cool: " + context.<Material>get("value")))
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("this"))
                .argument(LiteralArgument.of("command"))
                .argument(
                    IntegerArgument.<CommandSender>of("num")
                        .setSuggestionProvider((context, inputQueue) -> {
                            return Collections.singletonList(Suggestion.text("HAHAHAHA"));
                        })
                )
                .permission("example.number")
                .handler(context -> context.getSender().sendMessage("WHOA: " + context.<Integer>get("num")))
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("this"))
                .argument(LiteralArgument.of("command"))
                .argument(LiteralArgument.of("value"))
                .argument(StringArgument.of("word"))
                .handler(context -> context.getSender().sendMessage("WHOA: " + context.<String>get("word")))
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("this"))
                .argument(LiteralArgument.of("command"))
                .argument(LiteralArgument.of("value2"))
                .argument(StringArgument.of("word", StringArgument.StringMode.GREEDY))
                .handler(context -> context.getSender().sendMessage("WHOA: " + context.<String>get("word")))
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("this"))
                .argument(LiteralArgument.of("command"))
                .argument(LiteralArgument.of("value3"))
                .argument(StringArgument.of("word", StringArgument.StringMode.QUOTED))
                .handler(context -> {
                    context.getSender().sendMessage("MAIN: " + Bukkit.isPrimaryThread());
                    context.getSender().sendMessage("WHOA: " + context.<String>get("word"));
                })
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("this"))
                .argument(LiteralArgument.of("command"))
                .argument(LiteralArgument.of("value4"))
                .argument(StringArgument.of("word", StringArgument.StringMode.QUOTED))
                .argument(StringArgument.of("word2", StringArgument.StringMode.QUOTED))
                .executor(task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task))
                .handler(context -> {
                    context.getSender().sendMessage("MAIN: " + Bukkit.isPrimaryThread());
                    context.getSender().sendMessage("WHOA: " + context.<String>get("word"));
                    context.getSender().sendMessage("WHOA: " + context.<String>get("word2"));
                })
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("this"))
                .argument(LiteralArgument.of("command"))
                .argument(LocationArgument.of("location"))
                .handler(context -> {
                    final CommandLocation commandLocation = context.get("location");
                    context.getSender().sendMessage("WHOA: " + commandLocation);

                    if (context.getSender() instanceof final Player player) {
                        player.teleport(commandLocation.getLocation());
                    }
                })
        );
    }

    @Override
    public @NotNull PaperCommandMetadata.Builder createMetadata() {
        return PaperCommandMetadata.builder("example")
            .permission("example")
            .aliases("example2");
    }
}
