package net.polarizedions.polarizedbot.modules;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import discord4j.core.object.entity.User;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.config.BotConfig;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manages the modules that the bot runs.
 */
public class ModuleManager {
    private static final String MODULE_PATH = "net.polarizedions.polarizedbot.modules.impl";

    private CommandDispatcher<MessageSource> dispatcher;
    private List<IModule> modules;
    private List<IMessageRunner> messageRunners;
    private int activeCount = 0;


    public ModuleManager() {
        this.modules = new ArrayList<>();
        this.messageRunners = new ArrayList<>();

        this.registerModules();
    }

    /**
     * Loads all classes annotated with {@link PolarizedBotModule}
     */
    private void registerModules() {
        Reflections reflections = new Reflections(MODULE_PATH);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PolarizedBotModule.class);
        for (Class<?> annotatedClass : annotatedClasses) {
            try {
                Object reflectionInstance = annotatedClass.newInstance();
                if (reflectionInstance instanceof IModule) {
                    this.modules.add((IModule) reflectionInstance);
                    Bot.logger.info("Registered module: {}", annotatedClass.getSimpleName());
                }
            } catch (InstantiationException | IllegalAccessException e) {
                Bot.logger.error("Could not load module", e);
            }
        }
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
     *
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
            } catch (CommandSyntaxException e) {
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
