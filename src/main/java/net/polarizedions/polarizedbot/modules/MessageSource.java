package net.polarizedions.polarizedbot.modules;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.jetbrains.annotations.NotNull;

public class MessageSource {
    private Message message;
    private boolean privateMessage;

    public MessageSource(@NotNull MessageCreateEvent event) {
        this.message = event.getMessage();
        this.privateMessage = !event.getGuildId().isPresent();
    }

    public String getMessage() {
        return this.message.getContent().isPresent() ? this.message.getContent().get() : "";
    }

    public boolean isPrivateMessage() {
        return this.privateMessage;
    }

    public Message getWrapped() {
        return this.message;
    }
}
