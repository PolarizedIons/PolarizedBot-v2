package net.polarizedions.polarizedbot.modules;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import net.polarizedions.polarizedbot.Bot;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Util/wrapper class for {@link Message}. Used as a source for the command dispatcher.
 */
public class MessageSource {
    private Message message;
    private boolean privateMessage;
    private Snowflake guildId;
    private Bot bot;

    public MessageSource(Bot bot, @NotNull MessageCreateEvent event) {
        this.bot = bot;
        this.message = event.getMessage();
        this.privateMessage = !event.getGuildId().isPresent();
        this.guildId = event.getGuildId().isPresent() ? event.getGuildId().get() : Snowflake.of(-1);
    }

    /**
     * Get the textual content of the message.
     *
     * @return the text of the message
     */
    public String getMessage() {
        return this.message.getContent().isPresent() ? this.message.getContent().get() : "";
    }

    /**
     * Whether or not the message was sent privately. If false, it was sent in a guild.
     *
     * @return if the message was sent privately
     */
    public boolean isPrivateMessage() {
        return this.privateMessage;
    }

    /**
     * Get the wrapped message
     *
     * @return the original message
     */
    public Message getWrapped() {
        return this.message;
    }

    /**
     * Util method to reply textually to a message.
     *
     * @param text The text to reply with
     */
    public void reply(String text) {
        this.message.getChannel().subscribe(channel -> channel.createMessage(text).subscribe());
    }

    /**
     * Util method to reply with an embed to a message.
     *
     * @param specConsumer The EmbedSpec consumer
     */
    public void replyEmbed(Consumer<EmbedCreateSpec> specConsumer) {
        this.message.getChannel().subscribe(channel -> channel.createEmbed(specConsumer).subscribe());
    }

    /**
     * Get the user that sent the message. NOTE: can be none!
     *
     * @return the user
     */
    public Optional<User> getUser() {
        return this.message.getAuthor();
    }

    /**
     * Get the channel that the message was sent in.
     *
     * @return the channel
     */
    public Mono<MessageChannel> getChannel() {
        return this.message.getChannel();
    }

    /**
     * Get the instance of the bot
     *
     * @return the bot instance
     */
    public Bot getBot() {
       return this.bot;
    }

    /**
     * Shortcut method for {@link MessageSource#getBot()} -> {@link Bot#getClient()}.
     *
     * @return the discord client
     */
    public DiscordClient getClient() {
        return this.getBot().getClient();
    }

    /**
     * Get the guild id of the message. MAY BE null.
     *
     * @return the guild id
     */
    public Snowflake getGuildId() {
        return this.guildId;
    }
}
