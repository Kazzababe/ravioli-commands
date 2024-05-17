package ravioli.gravioli.example;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.command.EnumArgument;
import ravioli.gravioli.command.argument.command.IntegerArgument;
import ravioli.gravioli.command.argument.command.LiteralArgument;
import ravioli.gravioli.command.argument.command.StringArgument;
import ravioli.gravioli.command.paper.PaperCommand;
import ravioli.gravioli.command.paper.PaperCommandTrack;
import ravioli.gravioli.command.paper.metadata.PaperCommandMetadata;

public final class ExampleCommand extends PaperCommand {
    public ExampleCommand(@NotNull final Plugin plugin) {
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("throw"))
                .argument(EnumArgument.of("value", Material.class))
                .argument(EnumArgument.of("value2", Material.class))
                .permission("testing")
                .handler(context -> {
                    context.getSender().sendMessage(
                        "throw value = " + context.<Material>get("value")
                    );
                    context.getSender().sendMessage(
                        "throw value2 = " + context.<Material>get("value2")
                    );
                })
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("test"))
                .argument(IntegerArgument.optional("value"))
                .handler(context -> {
                    context.getSender().sendMessage(
                        "test value = " + context.<Integer>getOptional("value").orElse(0)
                    );
                })
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LiteralArgument.of("heck"))
                .argument(StringArgument.of("what"))
                .argument(IntegerArgument.optional("value"))
                .handler(context -> {
                    context.getSender().sendMessage(
                        "test value = " + context.<String>get("what") + ":" + context.<Integer>getOptional("value").orElse(0)
                    );
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
