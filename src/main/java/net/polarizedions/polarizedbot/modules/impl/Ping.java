package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;

import java.time.Duration;
import java.time.Instant;

import static net.polarizedions.polarizedbot.modules.BrigadierTypeFixer.literal;


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
                System.out.println("Starting " + start);
                channel.createEmbed(spec -> {
                    spec.setTitle(Language.get("ping.title"));
                    spec.addField(Language.get("ping.ping"), Language.get("ping.pong"), true);
                    System.out.println("created embed");
                }).subscribe(msg -> {
                    System.out.println("subbed received");
                    msg.edit(msgSpec -> {
                        System.out.println("editing");
                        msgSpec.setEmbed(embedSpec -> {
                            System.out.println("created embed edit");
                            Instant end = Instant.now();
                            Duration diff = Duration.between(start, end);

                            embedSpec.setTitle(Language.get("ping.title"));
                            embedSpec.addField(Language.get("ping.ping"), Language.get("ping.time", diff.toMillis()), true);
                        });
                    }).subscribe();
                });
            });

            return 1;
        }
    }
}
