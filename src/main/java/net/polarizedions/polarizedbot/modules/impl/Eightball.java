package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;
import net.polarizedions.polarizedbot.modules.PolarizedBotModule;
import net.polarizedions.polarizedbot.util.Colors;

import java.time.Instant;
import java.util.Random;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.argument;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.literal;

@PolarizedBotModule
public class Eightball implements IModule {
    private static final Random RANDOM = new Random();
    private ICommand command = new Command();

    @Override
    public ICommand getCommand() {
        return command;
    }

    private class Command implements ICommand {

        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(
                    literal("8ball").then(
                            argument("q", greedyString()).executes(c -> this.eightball(c.getSource(), getString(c, "q")))
                    )
            );

            dispatcher.register(
                    literal("eightball").then(
                            argument("q", greedyString()).executes(c -> this.eightball(c.getSource(), getString(c, "q")))
                    )
            );
        }

        private int eightball(MessageSource source, String q) {
            if (source.getMessage().endsWith("?")) {
                source.replyEmbed(spec -> {
                    spec.addField(q, Language.get("8ball.answer." + ( RANDOM.nextInt(20) + 1 )), true);

                    spec.setThumbnail("https://i.imgur.com/HvW2ZIB.png");
                    spec.setColor(Colors.INFO);
                    spec.setTimestamp(Instant.now());
                });
            }
            else {
                source.replyEmbed(spec -> {
                    spec.addField(q, Language.get("8ball.noQuestion"), true);

                    spec.setThumbnail("https://i.imgur.com/HvW2ZIB.png");
                    spec.setColor(Colors.BAD);
                    spec.setTimestamp(Instant.now());
                });
            }
            return 1;
        }
    }
}
