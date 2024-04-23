import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.CommandManager;
import ravioli.gravioli.command.CommandNode;
import ravioli.gravioli.command.CommandTrack;
import ravioli.gravioli.command.argument.IntegerArgument;
import ravioli.gravioli.command.argument.LiteralArgument;
import ravioli.gravioli.command.metadata.CommandMetadata;

public final class Test {
    public static void main(final String[] args) {
        final CommandManager<Object> commandManager = new CommandManager<>();

        commandManager.registerCommand(new ExampleCommand());
        final var result1 = commandManager.processCommand(new Object(), "test", "test this command");
        final var suggestions2 = commandManager.processSuggestions(new Object(), "test", "test this command valu");

        result1.getCommandExecution().run();
        System.out.println(suggestions2);
    }

    public static final class ExampleCommand extends Command<Object> {
        public ExampleCommand() {
            this.add(
                new CommandTrack.Builder<>()
                    .argument(LiteralArgument.of("this"))
                    .argument(LiteralArgument.of("command"))
                    .argument(IntegerArgument.of("num"))
                    .handler(context -> System.out.println("WHOA: " + context.<Integer>get("num")))
            );
            this.add(
                new CommandTrack.Builder<>()
                    .argument(LiteralArgument.of("this"))
                    .argument(LiteralArgument.of("command"))
                    .argument(IntegerArgument.of("num"))
                    .argument(IntegerArgument.of("num2"))
                    .handler(context -> System.out.println("WHOA: " + context.<Integer>get("num") + ", " + context.<Integer>get("num2")))
            );
            this.add(
                new CommandTrack.Builder<>()
                    .argument(LiteralArgument.of("this"))
                    .argument(LiteralArgument.of("command"))
                    .argument(LiteralArgument.of("value"))
                    .handler(context -> System.out.println("WHOA"))
            );
            this.add(
                new CommandTrack.Builder<>()
                    .argument(LiteralArgument.of("this"))
                    .argument(LiteralArgument.of("command"))
                    .argument(LiteralArgument.of("value2"))
                    .handler(context -> System.out.println("WHOA va2"))
            );
        }

        @Override
        public CommandMetadata.Builder<Object, ?> createMetadata() {
            return new CommandMetadata.Builder<>("test")
                .exceptionHandler((context, exception) -> {
                    final CommandNode<?, ?> node = exception.getNode();

                    if (node == null) {
                        return;
                    }
                    System.out.println("/" + node.getUsageString());
                })
                .aliases("testing");
        }
    }
}
