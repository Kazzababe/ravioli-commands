package ravioli.gravioli.example;

import org.bukkit.plugin.java.JavaPlugin;
import ravioli.gravioli.command.paper.PaperCommandManager;

public final class ExamplePlugin extends JavaPlugin {
    private final PaperCommandManager commandManager = new PaperCommandManager(this);

    @Override
    public void onLoad() {
        this.commandManager.enableBrigadierSupport();
    }

    @Override
    public void onEnable() {
        this.commandManager.registerCommand(new ExampleCommand(this));
    }
}
