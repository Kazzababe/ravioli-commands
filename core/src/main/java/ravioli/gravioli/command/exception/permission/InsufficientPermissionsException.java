package ravioli.gravioli.command.exception.permission;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.CommandNode;
import ravioli.gravioli.command.exception.CommandParseException;

@Getter
public class InsufficientPermissionsException extends CommandParseException {
    private final String permission;

    public InsufficientPermissionsException(@Nullable final CommandNode<?, ?> node, @NotNull final String permission) {
        super(node, "Insufficient permissions: Required permission is " + permission);

        this.permission = permission;
    }
}
