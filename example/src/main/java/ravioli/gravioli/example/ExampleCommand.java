package ravioli.gravioli.example;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.StringArgument;
import ravioli.gravioli.command.paper.PaperCommand;
import ravioli.gravioli.command.paper.PaperCommandTrack;
import ravioli.gravioli.command.paper.argument.PlayerArgument;
import ravioli.gravioli.command.paper.argument.location.CommandLocation;
import ravioli.gravioli.command.paper.argument.location.LocationArgument;
import ravioli.gravioli.command.paper.metadata.PaperCommandMetadata;

public final class ExampleCommand extends PaperCommand {
    public ExampleCommand(@NotNull final Plugin plugin) {
        this.add(
            PaperCommandTrack.command()
                .argument(StringArgument.of("value"))
                .argument(PlayerArgument.of("player"))
                .handler(context -> context.getSender().sendMessage("Cool: " + context.<String>get("value")))
        );
        this.add(
            PaperCommandTrack.command()
                .argument(LocationArgument.of("location"))
                .handler(context -> context.getSender().sendMessage("Cool with location: " + context.<CommandLocation>get("location")))
        );
        this.add(
            PaperCommandTrack.command()
                .argument(StringArgument.of("value"))
                .argument(LocationArgument.of("location"))
                .handler(context -> context.getSender().sendMessage("Cool value with location: " + context.<String>get("value") + ", " + context.<CommandLocation>get("location")))
        );
        this.add(
            PaperCommandTrack.command()
                .argument(StringArgument.of("value"))
                .argument(StringArgument.of("value2"))
                .handler(context -> context.getSender().sendMessage("Cool value with value2: " + context.<String>get("value") + ", " + context.<String>get("value2")))
        );
    }

    @Override
    public @NotNull PaperCommandMetadata.Builder createMetadata() {
        return PaperCommandMetadata.builder("example")
            .permission("example")
            .aliases("example2");
    }
}
