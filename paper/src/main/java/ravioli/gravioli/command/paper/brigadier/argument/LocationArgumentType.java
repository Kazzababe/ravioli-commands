package ravioli.gravioli.command.paper.brigadier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import ravioli.gravioli.command.paper.argument.location.CommandLocation;

public class LocationArgumentType implements ArgumentType<CommandLocation> {
    @Override
    public CommandLocation parse(final StringReader reader) {
        return null;
    }
}
