package ravioli.gravioli.command.exception.parse;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ravioli.gravioli.command.CommandNode;
import ravioli.gravioli.command.exception.CommandParseException;

@Getter
public final class PreparseFailureException extends CommandParseException {
    private final String argumentId;
    private final String input;

    public PreparseFailureException(@Nullable final CommandNode<?, ?> node, @NotNull final String argumentId, @NotNull final String input) {
        super(node, "Pre-parsing failed for argument '" + argumentId + "' with input: " + input);

        this.argumentId = argumentId;
        this.input = input;
    }
}
