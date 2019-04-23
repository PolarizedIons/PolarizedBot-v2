package net.polarizedions.polarizedbot.modules;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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

    public void reply(String text) {
        this.message.getChannel().subscribe(channel -> channel.createMessage(text).subscribe());
    }

    public void replyEmbed(Consumer<EmbedCreateSpec> specConsumer) {
        this.message.getChannel().subscribe(channel -> {
            channel.createEmbed(specConsumer).subscribe();
        });
    }

    @Nullable
    public User getUser() {
        return this.message.getAuthor().orElse(null);
    }
}
