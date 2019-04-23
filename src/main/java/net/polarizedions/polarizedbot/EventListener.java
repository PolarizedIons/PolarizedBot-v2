package net.polarizedions.polarizedbot;


import discord4j.core.DiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageEvent;
import net.polarizedions.polarizedbot.modules.MessageSource;
import org.jetbrains.annotations.NotNull;

public class EventListener {
    private Bot bot;

    public EventListener(@NotNull Bot bot) {
        this.bot = bot;
        EventDispatcher ed = bot.getClient().getEventDispatcher();

        ed.on(ReadyEvent.class).subscribe(this::onReadyEvent);
        ed.on(MessageCreateEvent.class).subscribe(this::onMessageEvent);
    }

    private void onReadyEvent(@NotNull ReadyEvent event) {
        System.out.println("Logged in as " + event.getSelf().getUsername() + "#" + event.getSelf().getDiscriminator());
    }

    private void onMessageEvent(MessageCreateEvent event) {
        System.out.println("MESSAGE: " + event);
        this.bot.getModuleManager().runMessage(new MessageSource(event));
    }
}
