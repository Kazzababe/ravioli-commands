package ravioli.gravioli.command.brigadier;

import lombok.Getter;
import lombok.Setter;
import ravioli.gravioli.command.CommandManager;

@Getter
public abstract class BrigadierCommandManager<T, K> extends CommandManager<T> {
    private final BrigadierParser<T, K> brigadierParser;

    @Setter
    private boolean enableBrigadierSupport;

    protected BrigadierCommandManager() {
        this.brigadierParser = this.createParser();
    }

    public boolean doesEnvironmentSupportBrigadier() {
        try {
            Class.forName("com.mojang.brigadier.tree.CommandNode");

            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    protected abstract BrigadierParser<T, K> createParser();
}
