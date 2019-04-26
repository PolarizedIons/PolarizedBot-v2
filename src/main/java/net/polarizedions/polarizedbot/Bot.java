package net.polarizedions.polarizedbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.modules.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bot {
    private DiscordClient client;
    private ModuleManager moduleManager;
    public static final Logger logger = LogManager.getLogger("PolarizedBot");

    Bot() {
        logger.info("Starting PolarizedBot v" + "TODO");
        logger.debug("Building client...");
        this.client = new DiscordClientBuilder(BotConfig.get().discordToken).build();

        logger.debug("Loading modules...");
        this.moduleManager = new ModuleManager();

        logger.debug("Registering event listeners...");
        new EventListener(this);
    }

    private void run() {
        logger.info("Logging in...");
        this.client.login().block();
    }

    public DiscordClient getClient() {
        return client;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static void main(String[] args) {
        new Bot().run();
    }
}
