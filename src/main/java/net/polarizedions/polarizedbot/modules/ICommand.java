package net.polarizedions.polarizedbot.modules;

import com.mojang.brigadier.CommandDispatcher;

public interface ICommand {
    void register(CommandDispatcher<MessageSource> dispatcher);
}
