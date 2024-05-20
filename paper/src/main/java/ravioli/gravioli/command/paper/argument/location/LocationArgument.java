package ravioli.gravioli.command.paper.argument.location;

import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.argument.CommandArgumentType;
import ravioli.gravioli.command.argument.command.CommandArgument;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.CommandParseException;
import ravioli.gravioli.command.parse.StringTraverser;

import java.util.Collections;
import java.util.List;

public final class LocationArgument extends CommandArgument<CommandSender, Coordinates> {
    public static @NotNull LocationArgumentBuilder of(@NotNull final String id) {
        return new LocationArgumentBuilder(id);
    }

    private static final String RELATIVE_PREFIX = "~";
    private static final String LOCAL_PREFIX = "^";

    private LocationArgument(@NotNull final String id) {
        super(id);
    }

    @Override
    protected @NotNull List<Suggestion> parseSuggestions(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        return Collections.emptyList(); // There isn't straight forward way to give vector arguments using paper's API so we just return nothing here
    }

    @Override
    public @NotNull Coordinates parse(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) throws CommandParseException {
        final CoordinateResult x = this.parseCoordinate(traverser);
        final CoordinateResult y = this.parseCoordinate(traverser);
        final CoordinateResult z = this.parseCoordinate(traverser);
        final Location origin;

        if (context.getSender() instanceof final Entity entity) {
            origin = entity.getLocation().clone();
        } else {
            origin = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }
        return x.coordinateType == CommandLocation.CoordinateType.LOCAL ?
            new LocalCoordinates(x.value, y.value, z.value) :
            new WorldCoordinates(
                new WorldCoordinate(x.coordinateType == CommandLocation.CoordinateType.RELATIVE, x.value),
                new WorldCoordinate(y.coordinateType == CommandLocation.CoordinateType.RELATIVE, y.value),
                new WorldCoordinate(z.coordinateType == CommandLocation.CoordinateType.RELATIVE, z.value)
            );
    }

    @Override
    public boolean isValidForSuggestions(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        if (!traverser.hasNext()) {
            return true;
        }
        int argumentCount = 0;
        boolean previousLocal = false;

        while (traverser.hasNext() && argumentCount < 3) {
            traverser.traverseWhitespace();

            if (!traverser.hasNext()) {
                break;
            }
            final String potentialArg = traverser.readString();
            final String currentArg = this.getCoordinatePrefix(potentialArg);
            final boolean isLocal = currentArg != null && currentArg.equals(LOCAL_PREFIX);

            if (currentArg == null || (argumentCount > 0 && isLocal != previousLocal)) {
                return false;
            }
            previousLocal = isLocal;
            argumentCount++;
        }
        return true;
    }

    @Override
    public boolean isValidForExecution(@NotNull final CommandContext<CommandSender> context, @NotNull final StringTraverser traverser) {
        if (!traverser.hasNext()) {
            return true;
        }
        int argumentCount = 0;
        boolean previousLocal = false;

        while (traverser.hasNext() && argumentCount < 3) {
            traverser.traverseWhitespace();

            if (!traverser.hasNext()) {
                break;
            }
            final String potentialArg = traverser.readString();
            final String currentArg = this.getCoordinatePrefix(potentialArg);
            final boolean isLocal = currentArg != null && currentArg.equals(LOCAL_PREFIX);

            if (currentArg == null || (argumentCount > 0 && isLocal != previousLocal)) {
                return false;
            }
            previousLocal = isLocal;
            argumentCount++;
        }
        return argumentCount >= 3;
    }

    @Override
    public @NotNull CommandArgumentType getType() {
        return CommandArgumentType.GREEDY_STRING;
    }

    @Override
    public boolean shouldDefaultSuggestionsToBrigadier() {
        return false;
    }

    public static final class LocationArgumentBuilder extends CommandArgumentBuilder<CommandSender, Coordinates, LocationArgument, LocationArgumentBuilder> {
        private LocationArgumentBuilder(@NotNull final String id) {
            super(id);
        }

        @Override
        protected LocationArgument createArgument() {
            return new LocationArgument(this.id);
        }
    }

    private @Nullable String getCoordinatePrefix(@NotNull String arg) {
        String prefix = "";

        if (arg.startsWith(RELATIVE_PREFIX) || arg.startsWith(LOCAL_PREFIX)) {
            prefix = String.valueOf(arg.charAt(0));
            arg = arg.substring(1);
        }
        try {
            if (!arg.isBlank()) {
                Double.parseDouble(arg);
            }
            return prefix;
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    private @NotNull CoordinateResult parseCoordinate(final StringTraverser inputQueue) throws CommandParseException {
        inputQueue.traverseWhitespace();
        final String input = inputQueue.readString();
        final CommandLocation.CoordinateType coordinateType = input.startsWith(RELATIVE_PREFIX) ?
            CommandLocation.CoordinateType.RELATIVE :
            input.startsWith(LOCAL_PREFIX) ?
                CommandLocation.CoordinateType.LOCAL :
                CommandLocation.CoordinateType.ABSOLUTE;

        if (coordinateType != CommandLocation.CoordinateType.ABSOLUTE) {
            final String relativeValue = input.substring(1);

            if (relativeValue.isEmpty()) {
                return new CoordinateResult(0, coordinateType);
            }
            try {
                return new CoordinateResult(Double.parseDouble(relativeValue), coordinateType);
            } catch (final NumberFormatException e) {
                throw new CommandParseException(relativeValue);
            }
        }
        try {
            return new CoordinateResult(Double.parseDouble(input), CommandLocation.CoordinateType.ABSOLUTE);
        } catch (final NumberFormatException e) {
            throw new CommandParseException(input);
        }

    }

    private static class CoordinateResult {
        private final double value;
        private final CommandLocation.CoordinateType coordinateType;

        private CoordinateResult(final double value, final CommandLocation.CoordinateType coordinateType) {
            this.value = value;
            this.coordinateType = coordinateType;
        }
    }
}
