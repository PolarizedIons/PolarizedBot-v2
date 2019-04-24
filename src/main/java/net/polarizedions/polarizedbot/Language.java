package net.polarizedions.polarizedbot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.internal.util.xml.impl.Input;
import net.polarizedions.polarizedbot.config.BotConfig;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language {
    private static Map<String, String> LANGUAGE_MAP = new HashMap<>();
    private static Language instance;

    private void init() {

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

    public static Language get() {
        if (instance == null) {
            instance = new Language();
            instance.init();
        }

        return instance;
    }

    public static String get(String key, Object... context) {
        return get().translate(key, context);
    }

    public String translate(String key, Object... context) {
        return String.format(LANGUAGE_MAP.getOrDefault(key, key), context);
    }
}
