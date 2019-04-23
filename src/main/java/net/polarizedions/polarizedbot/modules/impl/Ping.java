package net.polarizedions.polarizedbot.modules.impl;

import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;

import static net.polarizedions.polarizedbot.modules.BrigadierTypeFixer.literal;


public class Ping implements IModule {
    @Override
    public ICommand getCommand() {
        return dispatcher -> dispatcher.register(
                literal("ping")
                    .executes(c -> {System.out.println("pong"); return 1;})
        );
    }
}
