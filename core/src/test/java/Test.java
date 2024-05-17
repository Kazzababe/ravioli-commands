import ravioli.gravioli.command.Command;
import ravioli.gravioli.command.CommandManager;
import ravioli.gravioli.command.argument.CommandTrack;
import ravioli.gravioli.command.argument.command.EnumArgument;
import ravioli.gravioli.command.argument.command.IntegerArgument;
import ravioli.gravioli.command.argument.command.LiteralArgument;
import ravioli.gravioli.command.argument.command.StringArgument;
import ravioli.gravioli.command.metadata.CommandMetadata;

public final class Test {
    public static void main(final String[] args) {
        final CommandManager<Object> commandManager = new CommandManager<>() {
            @Override
            protected boolean hasPermission(final Object sender, final String permission) {
                return true;
            }
        };

        commandManager.registerCommand(new ExampleCommand());
        final var result1 = commandManager.parseCommand(new Object(), "example throw ");
        final var suggestions2 = commandManager.getSuggestions(new Object(), "example throw ");

        result1.getCommandExecution().run();
        System.out.println(suggestions2);
    }

    public static final class ExampleCommand extends Command<Object> {
        public ExampleCommand() {
            this.add(
                new CommandTrack.Builder<>()
                    .argument(LiteralArgument.of("throw"))
                    .argument(IntegerArgument.optional("value"))
                    .handler(context -> System.out.println("WHOA: " + context.<StringArgument.StringMode>getOptional("num").orElse(StringArgument.StringMode.WORD)))
            );
            this.add(
                new CommandTrack.Builder<>()
                    .argument(LiteralArgument.of("test"))
                    .argument(IntegerArgument.optional("value"))
                    .handler(context -> System.out.println("WHOA 1: " + context.<Integer>get("num")))
            );
            this.add(
                new CommandTrack.Builder<>()
                    .argument(LiteralArgument.of("test"))
                    .argument(StringArgument.of("what"))
                    .argument(IntegerArgument.optional("value"))
                    .handler(context -> System.out.println("WHOA 2: " + context.<Integer>get("num")))
            );
        }

        @Override
        public CommandMetadata.Builder<Object, ?> createMetadata() {
            return new CommandMetadata.Builder<>("example")
                .exceptionHandler((context, exception) -> {
                    exception.printStackTrace();
//                    final CommandNode<?, ?> node = exception.getNode();
//
//                    if (node == null) {
//                        return;
//                    }
//                    System.out.println("/" + node.getUsageString());
                })
                .aliases("testing");
        }
    }
}
