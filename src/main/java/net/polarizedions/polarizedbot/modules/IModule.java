package net.polarizedions.polarizedbot.modules;

public interface IModule {
    /**
     * Get the command for this module.
     * NOTE: It SHOULD return the same object each time this method is called.
     *
     * @return The command for the module.
     */
    default ICommand getCommand() {
        return null;
    }

    /**
     * Gets the message runner for this module
     * NOTE: It SHOULD return this same object each time this method is called.
     *
     * @return The message runner for this module
     */
    default IMessageRunner getMessageRunner() {
        return null;
    }

    // TODO: module config

    /**
     * Whether this module will process messages. Default to true.
     *
     * @return Whether the module is active.
     */
    default boolean isActive() {
        return true; // TODO: load from module config
    }

    default void startup() { }

    default void shutdown() { }
}
