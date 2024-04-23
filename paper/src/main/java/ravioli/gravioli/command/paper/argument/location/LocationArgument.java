package ravioli.gravioli.command.paper.argument.location;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.Argument;
import ravioli.gravioli.command.argument.ArgumentParser;
import ravioli.gravioli.command.argument.suggestion.Suggestion;
import ravioli.gravioli.command.argument.suggestion.SuggestionProvider;
import ravioli.gravioli.command.context.CommandContext;
import ravioli.gravioli.command.exception.ArgumentParseException;
import ravioli.gravioli.command.exception.parse.InvalidLocationFormatException;
import ravioli.gravioli.command.parse.StringTraverser;
import ravioli.gravioli.command.parse.result.ArgumentParseResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LocationArgument extends Argument<CommandLocation, LocationArgument.LocationArgumentParser, CommandSender, LocationArgument> {
    public static @NotNull LocationArgument of(@NotNull final String id) {
        return new LocationArgument(id);
    }

    private static final String RELATIVE_PREFIX = "~";

    private final LocationArgumentParser parser;

    private LocationArgument(@NotNull final String id) {
        super(id);

        this.parser = new LocationArgumentParser(this);
    }

    @Override
    public int getPriority() {
        return 300;
    }

    @NotNull
    @Override
    public LocationArgumentParser getParser() {
        return this.parser;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationArgumentParser extends ArgumentParser<CommandLocation, CommandSender> {
        private final LocationArgument argument;

        @Override
        public boolean preParse(@NotNull final CommandContext<CommandSender> commandContext, @NotNull final StringTraverser inputQueue) {
            if (!inputQueue.hasNext()) {
                return true;
            }
            int argumentCount = 0;

            while (inputQueue.hasNext()) {
                if (argumentCount > 3) {
                    return false;
                }
                inputQueue.traverseWhitespace();

                if (!inputQueue.hasNext()) {
                    break;
                }
                final String potentialArg = inputQueue.readString();

                if (!this.isValidCoordinateArgument(potentialArg)) {
                    return false;
                }
                argumentCount++;
            }
            return argumentCount >= 0 && argumentCount <= 3;
        }

        @Override
        public @NotNull ArgumentParseResult<CommandLocation> parse(@NotNull final CommandContext<CommandSender> commandContext, @NotNull final StringTraverser inputQueue){
            final CoordinateResult x;
            final CoordinateResult y;
            final CoordinateResult z;

            try {
                x = this.parseCoordinate(inputQueue);
                y = this.parseCoordinate(inputQueue);
                z = this.parseCoordinate(inputQueue);
            } catch (final ArgumentParseException e) {
                return ArgumentParseResult.failure(e);
            }
            final Location origin;

            if (commandContext.getSender() instanceof final Entity entity) {
                origin = entity.getLocation().clone();
            } else {
                origin = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
            }
            final Location location = new Location(origin.getWorld(),
                x.coordinateType == CommandLocation.CoordinateType.ABSOLUTE ? x.value : origin.getX() + x.value,
                y.coordinateType == CommandLocation.CoordinateType.ABSOLUTE ? y.value : origin.getY() + y.value,
                z.coordinateType == CommandLocation.CoordinateType.ABSOLUTE ? z.value : origin.getZ() + z.value,
                origin.getYaw(),
                origin.getPitch()
            );
            final Triple<Double, Double, Double> respectiveCoordinates = Triple.of(x.value, y.value, z.value);
            final Triple<CommandLocation.CoordinateType, CommandLocation.CoordinateType, CommandLocation.CoordinateType> respectiveCoordinateTypes =
                Triple.of(x.coordinateType, y.coordinateType, z.coordinateType);

            return ArgumentParseResult.success(new CommandLocation(origin, location, respectiveCoordinates, respectiveCoordinateTypes));
        }

        @Override
        public @NotNull SuggestionProvider<CommandSender> getSuggestionProvider() {
            return Optional.ofNullable(this.argument.getSuggestionProvider())
                .orElseGet(() ->
                    (context, inputQueue) -> {
                        final List<Suggestion> suggestions = new ArrayList<>();
                        final List<String> enteredCoords = new ArrayList<>();
                        final List<String> whitespaceBetween = new ArrayList<>();
                        final StringBuilder previousInput = new StringBuilder();

                        while (inputQueue.hasNext() && enteredCoords.size() < 3) {
                            final int whitespace = inputQueue.traverseWhitespace();
                            final String spacer = StringUtils.repeat(' ', whitespace);

                            if (!previousInput.isEmpty()) {
                                previousInput.append(spacer);
                            }
                            whitespaceBetween.add(spacer);

                            if (!inputQueue.hasNext()) {
                                break;
                            }
                            final String coordinate = inputQueue.readString();

                            enteredCoords.add(coordinate);
                            previousInput.append(coordinate);
                        }
                        final String input = previousInput.toString();
                        final StringBuilder suggestionBuilder = new StringBuilder(input);

                        for (int i = enteredCoords.size(); i < 3; i++) {
                            if (i > 0) {
                                suggestionBuilder.append(i < whitespaceBetween.size() ? whitespaceBetween.get(i - 1) : " ");
                            }
                            suggestionBuilder.append(RELATIVE_PREFIX);
                        }
                        final String suggestion = suggestionBuilder.toString();

                        suggestions.add(Suggestion.replaceText(input, suggestion));

                        return suggestions;
                    }
                );
        }

        private boolean isValidCoordinateArgument(@NotNull String arg) {
            if (arg.startsWith(RELATIVE_PREFIX)) {
                arg = arg.substring(1);
            }
            if (arg.isBlank()) {
                return true;
            }
            try {
                Double.parseDouble(arg);

                return true;
            } catch (final NumberFormatException e) {
                return false;
            }
        }

        private @NotNull CoordinateResult parseCoordinate(final StringTraverser inputQueue) throws ArgumentParseException {
            inputQueue.traverseWhitespace();
            final String input = inputQueue.readString();

            if (input.startsWith(RELATIVE_PREFIX)) {
                final String relativeValue = input.substring(RELATIVE_PREFIX.length());

                if (relativeValue.isEmpty()) {
                    return new CoordinateResult(0, CommandLocation.CoordinateType.RELATIVE);
                }
                try {
                    return new CoordinateResult(Double.parseDouble(relativeValue), CommandLocation.CoordinateType.RELATIVE);
                } catch (final NumberFormatException e) {
                    throw new InvalidLocationFormatException(relativeValue);
                }
            }
            try {
                return new CoordinateResult(Double.parseDouble(input), CommandLocation.CoordinateType.ABSOLUTE);
            } catch (final NumberFormatException e) {
                throw new InvalidLocationFormatException(input);
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
}
