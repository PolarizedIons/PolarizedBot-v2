package net.polarizedions.polarizedbot.modules;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BrigadierTypeFixer {
    // Thank you pokechu22 in #mcdevs on Freenode for helping me figure this out

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * LiteralArgumentBuilder#literal(String)}  method is that it is typed to {@link MessageSource}.
     */
    @NotNull
    @Contract(pure = true)
    public static LiteralArgumentBuilder<MessageSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * RequiredArgumentBuilder#argument(String, ArgumentType)} method is that it is typed to {@link MessageSource}.
     */
    @NotNull
    @Contract(pure = true)
    public static <T> RequiredArgumentBuilder<MessageSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
