package net.polarizedions.polarizedbot.modules;

import com.mojang.brigadier.CommandDispatcher;

public interface ICommand {
    /**
     * Register a command to the command dispatcher.
     * NOTE: May be called multiple times during the application lifetime.
     *
     * @param dispatcher The command dispatcher.
     */
    void register(CommandDispatcher<MessageSource> dispatcher);
}
