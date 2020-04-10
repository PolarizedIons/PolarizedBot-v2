package net.polarizedions.polarizedbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.modules.ModuleManager;
import net.polarizedions.polarizedbot.util.BuildInfo;
import net.polarizedions.polarizedbot.util.PresenceUpdator;
import net.polarizedions.polarizedbot.util.Uptime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bot {
    private DiscordClient client;
    private ModuleManager moduleManager;
    public static final Logger logger = LogManager.getLogger("PolarizedBot");

    Bot() {
        logger.info("Starting PolarizedBot v" + BuildInfo.version);
        logger.debug("Building client...");
        this.client = DiscordClientBuilder.create(BotConfig.get().discordToken).build();

        logger.debug("Loading modules...");
        this.moduleManager = new ModuleManager();

        logger.debug("Registering event listeners...");
        new EventListener(this);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");
            this.moduleManager.shutdown();
        }));
    }

    private void run() {
        logger.info("Logging in...");
        this.client.login().subscribe();
        new PresenceUpdator(this.getClient());
    }

    public DiscordClient getClient() {
        return client;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static void main(String[] args) {
        Uptime.start();
        new Bot().run();
    }
}
