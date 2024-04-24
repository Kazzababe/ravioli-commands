package ravioli.gravioli.command.paper;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.CommandTrack;
import ravioli.gravioli.command.paper.metadata.PaperCommandMetadata;

public abstract class PaperCommand extends Command<CommandSender> {
    @Override
    protected final void add(@NotNull final CommandTrack<CommandSender> commandTrack) {
        super.add(commandTrack);
    }

    @Override
    protected final void add(@NotNull final CommandTrack.Builder<CommandSender> commandTrackBuilder) {
        super.add(commandTrackBuilder);
    }

    @Override
    public abstract @NotNull PaperCommandMetadata.Builder createMetadata();
}
