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
public class Rate implements IModule {
    private static final Random RANDOM = new Random();
    private ICommand command = new Command();

    @Override
    public ICommand getCommand() {
        return command;
    }

    private class Command implements ICommand {
        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(literal("rate")
                    .then(argument("q", greedyString()).executes(c -> this.rate(c.getSource(), getString(c, "q"))))
            );
        }

        private int rate(MessageSource source, String rateThing) {
            boolean isRandom = RANDOM.nextInt(50) == 1;
            final int rating;
            if (isRandom) {
                rating = RANDOM.nextInt(10);
            }
            else {
                int sum = 0;
                for (char c : rateThing.toCharArray()) {
                    sum += c;
                }
                RANDOM.setSeed(sum);
                rating = RANDOM.nextInt(10);
            }

            source.replyEmbed(spec -> {
                spec.setTitle(rateThing);
                spec.addField(Language.get("rate.title"), Language.get("rate.rating", rating), true);

                spec.setColor(Colors.INFO);
                spec.setTimestamp(Instant.now());
            });

            return 1;
        }
    }
}
