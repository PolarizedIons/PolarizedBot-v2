package net.polarizedions.polarizedbot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.modules.ModuleManager;

public class Bot {
    private DiscordClient client;
    private ModuleManager moduleManager;

    Bot() {
        this.client = new DiscordClientBuilder(BotConfig.get().discordToken).build();
        this.moduleManager = new ModuleManager();

        new EventListener(this);
    }

    private void run() {
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
