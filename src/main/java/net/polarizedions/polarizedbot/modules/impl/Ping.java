package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;

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
            source.reply("Pong!");
            source.replyEmbed(spec -> {
                spec.setTitle("PONG");
                spec.addField("ping", "pong", true);
            });

            return 1;
        }
    }
}
