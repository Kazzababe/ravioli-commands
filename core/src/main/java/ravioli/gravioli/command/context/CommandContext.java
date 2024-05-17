package ravioli.gravioli.command.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class CommandContext<T> {
    @Getter
    private final T sender;

    private final Map<String, Object> values = new HashMap<>();

    public CommandContext(@NotNull final CommandContext<T> commandContext) {
        this(commandContext.sender);

        this.values.putAll(commandContext.values);
    }

    public void supply(@NotNull final String key, @NotNull final Object value) {
        this.values.put(key, value);
    }

    public <K> @NotNull K get(@NotNull final String key) {
        return Objects.requireNonNull((K) this.values.get(key));
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
