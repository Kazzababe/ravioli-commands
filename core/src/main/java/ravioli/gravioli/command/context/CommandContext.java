package ravioli.gravioli.command.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public final class CommandContext<T> {
    @Getter
    private final T sender;

    @Getter
    private final Command<T> command;

    private final Map<String, Object> values = new HashMap<>();

    public CommandContext(@NotNull final CommandContext<T> commandContext) {
        this(commandContext.sender, commandContext.command);

        this.values.putAll(commandContext.values);
    }

    public void supply(@NotNull final String key, @NotNull final Object value) {
        this.values.put(key, value);
    }

    public <K> @NotNull K get(@NotNull final String key) {
        return (K) this.values.get(key);
    }

    public <K> @NotNull Optional<K> getOptional(@NotNull final String key) {
        return Optional.ofNullable(this.values.get(key))
            .map(value -> (K) value);
    }

    public void apply(@NotNull final CommandContext<T> other) {
        other.values.clear();
        other.values.putAll(this.values);
    }
}
