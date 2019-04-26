package net.polarizedions.polarizedbot.modules.brigadier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DiscordPing implements ArgumentType<Long> {
    @Override
    public Long parse(StringReader stringReader) throws CommandSyntaxException {
        stringReader.expect('<');
        stringReader.expect('@');
        if (stringReader.peek() == '!') {
            stringReader.expect('!');
        }

        long discordId = stringReader.readLong();
        stringReader.expect('>');

        return discordId;
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static DiscordPing discordPing() {
        return new DiscordPing();
    }
    public
    static long getDiscordPing(@NotNull final CommandContext<?> context, final String name) {
        return context.getArgument(name, Long.class);
    }

    @Override
    public String toString() {
        return "discordPing()";
    }
}
