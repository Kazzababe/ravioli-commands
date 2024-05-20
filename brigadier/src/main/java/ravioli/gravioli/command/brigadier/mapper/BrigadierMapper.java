package ravioli.gravioli.command.brigadier.mapper;

import com.mojang.brigadier.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;
import ravioli.gravioli.command.argument.command.CommandArgument;

@SuppressWarnings("rawtypes")
@FunctionalInterface
public interface BrigadierMapper<A extends CommandArgument> {
    @NotNull ArgumentType<?> convert(@NotNull A argument);
}
