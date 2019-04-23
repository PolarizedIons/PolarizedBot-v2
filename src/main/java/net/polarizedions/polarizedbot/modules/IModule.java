package net.polarizedions.polarizedbot.modules;

public interface IModule {
    default ICommand getCommand() {
        return null;
    }

    default IMessageRunner getMessageRunner() {
        return null;
    }

    // TODO: module config

    default boolean isActive() {
        return true; // TODO: load from module config
    }
}
