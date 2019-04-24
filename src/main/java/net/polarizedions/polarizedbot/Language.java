package net.polarizedions.polarizedbot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.polarizedions.polarizedbot.config.BotConfig;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Translates strings into a defined language. Language files must be json, and stored under `/lang/`. Sub-objects are
 * separated by dots (.) when getting values from the class.
 */
public class Language {
    private static Map<String, String> LANGUAGE_MAP;
    private static Language instance;

    /**
     * Load the language file specified in {@link BotConfig#lang} to be used. Language CANNOT be changed during runtime.
     */
    private void init() {
        LANGUAGE_MAP = new HashMap<>();
        JsonObject obj = new JsonParser().parse(new InputStreamReader(getClass().getResourceAsStream("/lang/" + BotConfig.get().lang + ".json"))).getAsJsonObject();
        parseTree("", obj);
    }

    private void parseTree(String prefix, @NotNull JsonObject obj) {
        prefix = prefix.isEmpty() ? "" : prefix + ".";

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = prefix + entry.getKey();
            if (entry.getValue().isJsonObject()) {
                parseTree(key, entry.getValue().getAsJsonObject());
            }
            else {
                LANGUAGE_MAP.put(key, entry.getValue().getAsString());
            }
        }
    }

    /**
     * Get the instance of the class. Initializes it if it hasn't before.
     *
     * @return The {@link Language} instance.
     */
    public static Language get() {
        if (instance == null) {
            instance = new Language();
            instance.init();
        }

        return instance;
    }

    /**
     * Shortcut method combining {@link Language#get()} and {@link Language#translate(String, Object...)}.
     * Gets the instance then gets the translated value from the language file.
     *
     * @param key The key for the translation process
     * @param context (Optional) Values to be filled in by the translation process
     * @return The translated string
     */
    public static String get(String key, Object... context) {
        return get().translate(key, context);
    }

    /**
     * Translates the translated value from the language file.
     *
     * @param key The key for the translation process
     * @param context (Optional) Values to be filled in by the translation process
     * @return The translated string
     */
    public String translate(String key, Object... context) {
        return String.format(LANGUAGE_MAP.getOrDefault(key, key), context);
    }
}
