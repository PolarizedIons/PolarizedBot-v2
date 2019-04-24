package net.polarizedions.polarizedbot.modules;

public interface IMessageRunner {
    /**
     * Run the message runner on a message.
     *
     * @param source The message.
     */
    void run(MessageSource source);
}
