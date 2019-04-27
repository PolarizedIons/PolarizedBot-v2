package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;
import net.polarizedions.polarizedbot.modules.PolarizedBotModule;
import net.polarizedions.polarizedbot.util.Colors;

import java.time.Duration;
import java.time.Instant;

import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.literal;

@PolarizedBotModule
public class Ping implements IModule {
    private ICommand command = new Command();

    @Override
    public ICommand getCommand() {
        return command;
    }

    private class Command implements ICommand {
        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(
                    literal("ping")
                            .executes(c -> this.ping(c.getSource()))
            );
        }

        int ping(MessageSource source) {
            source.getChannel().subscribe(channel -> {
                Instant start = Instant.now();
                channel.createEmbed(spec -> {
                    spec.addField(Language.get("ping.ping"), Language.get("ping.pong"), true);

                    spec.setColor(Colors.NEUTRAL);
                    spec.setTimestamp(Instant.now());
                }).subscribe(msg -> {
                    msg.edit(msgSpec -> {
                        msgSpec.setEmbed(embedSpec -> {
                            Instant end = Instant.now();
                            Duration diff = Duration.between(start, end);

                            embedSpec.addField(Language.get("ping.ping"), Language.get("ping.time", diff.toMillis()), true);

                            embedSpec.setColor(Colors.INFO);
                            embedSpec.setTimestamp(Instant.now());
                        });
                    }).subscribe();
                });
            });

            return 1;
        }
    }
}
