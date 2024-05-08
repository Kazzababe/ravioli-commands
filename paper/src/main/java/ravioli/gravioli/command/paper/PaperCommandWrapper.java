package ravioli.gravioli.command.paper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.parse.result.CommandParseResult;

import java.util.List;

public final class PaperCommandWrapper extends Command {
    private final PaperCommandManager commandManager;
    private final ravioli.gravioli.command.Command<CommandSender> ravioliCommand;

    public PaperCommandWrapper(@NotNull final PaperCommandManager commandManager, @NotNull final ravioli.gravioli.command.Command<CommandSender> ravioliCommand) {
        super(ravioliCommand.getCommandMetadata().getName().toLowerCase());

        this.setAliases(List.of(ravioliCommand.getCommandMetadata().getAliases()));
        this.setPermission(ravioliCommand.getCommandMetadata().getPermission());
        this.commandManager = commandManager;
        this.ravioliCommand = ravioliCommand;
    }

    @Override
    public boolean execute(@NotNull final CommandSender commandSender, @NotNull final String s, final @NotNull String[] strings) {
        final String command = this.ravioliCommand.getCommandMetadata().getName() + " " + String.join(" ", strings).trim();
        final CommandParseResult<CommandSender> result = this.commandManager.processCommand(commandSender, this.ravioliCommand, command);

        result.getCommandExecution().run();

        return true;
    }


}
