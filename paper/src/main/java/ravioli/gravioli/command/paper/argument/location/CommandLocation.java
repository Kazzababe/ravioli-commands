package ravioli.gravioli.command.paper.argument.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class CommandLocation  {
    @Getter
    private final Location origin;

    @Getter
    private final Location location;

    private final Triple<Double, Double, Double> respectiveCoordinates;
    private final Triple<CoordinateType, CoordinateType, CoordinateType> respectiveCoordinateTypes;

    public double getInputX() {
        return this.respectiveCoordinates.getLeft();
    }

    public double getInputY() {
        return this.respectiveCoordinates.getMiddle();
    }

    public double getInputZ() {
        return this.respectiveCoordinates.getRight();
    }

    public @NotNull CoordinateType getCoordinateTypeX() {
        return this.respectiveCoordinateTypes.getLeft();
    }

    public @NotNull CoordinateType getCoordinateTypeY() {
        return this.respectiveCoordinateTypes.getMiddle();
    }

    public @NotNull CoordinateType getCoordinateTypeZ() {
        return this.respectiveCoordinateTypes.getRight();
    }

    public enum CoordinateType {
        ABSOLUTE,
        RELATIVE;
    }
}
