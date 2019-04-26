package net.polarizedions.polarizedbot.modules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.modules.impl.About;
import net.polarizedions.polarizedbot.modules.impl.Ping;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the modules that the bot runs.
 */
public class ModuleManager {
    private CommandDispatcher<MessageSource> dispatcher;
    private List<IModule> modules;
    private List<IMessageRunner> messageRunners;
    private int activeCount = 0;


    public ModuleManager() {
        this.modules = new ArrayList<>();
        this.messageRunners = new ArrayList<>();

        this.modules.add(new About());
        this.modules.add(new Ping());
        this.refreshActiveModules();
    }

    /**
     * Refresh which modules are considered active by the command dispatcher & the message runners.
     */
    private void refreshActiveModules() {
        this.dispatcher = new CommandDispatcher<>();
        this.messageRunners.clear();
        activeCount = 0;

        for (IModule module : this.modules) {
            if (module.isActive()) {
                activeCount += 1;
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

    /**
     * Run the message through the command dispatcher (if it starts with the command prefix)
     * @param source
     */
    public void runMessage(@NotNull MessageSource source) {
        User user = source.getUser().orElse(null);
        if (user != null && user.isBot()) {
            return;
        }

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

    public int getModuleCount() {
        return modules.size();
    }

    public int getActiveModuleCount() {
        return this.activeCount;
    }
}
