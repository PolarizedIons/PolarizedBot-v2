package net.polarizedions.polarizedbot.modules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.modules.impl.Ping;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private CommandDispatcher<MessageSource> dispatcher;
    private List<IModule> modules;
    private List<IMessageRunner> messageRunners;


    public ModuleManager() {
        this.modules = new ArrayList<>();
        this.messageRunners = new ArrayList<>();

        this.modules.add(new Ping());
        this.refreshActiveModules();
    }

    private void refreshActiveModules() {
        this.dispatcher = new CommandDispatcher<>();
        this.messageRunners.clear();

        for (IModule module : this.modules) {
            if (module.isActive()) {
                ICommand command = module.getCommand();
                IMessageRunner messageRunner = module.getMessageRunner();

                if (command != null) {
                    command.register(this.dispatcher);
                }

                if (messageRunner != null) {
                    this.messageRunners.add(messageRunner);
                }
            }
        }
    }

    public void runMessage(MessageSource source) {
        String botPrefix = BotConfig.get().prefix;
        if (source.getMessage().startsWith(botPrefix)) {
            try {
                dispatcher.execute(source.getMessage().substring(botPrefix.length()), source);
                return;
            }
            catch (CommandSyntaxException e) {
                // NOOP
            }
        }

        for (IMessageRunner runner : this.messageRunners) {
            runner.run(source);
        }
    }
}
