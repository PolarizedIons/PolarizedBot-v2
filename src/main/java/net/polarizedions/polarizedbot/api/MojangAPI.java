package net.polarizedions.polarizedbot.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.api.apiutil.HTTPRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class MojangAPI {
    private static final String NAME_TO_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String UUID_TO_NAME_HISTORY_URL = "https://api.mojang.com/user/profiles/%s/names";
    private static final String UUID_TO_PROFILE = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");


    public static UUID getPlayerUUID(String name) {
        JsonObject json = HTTPRequest.GET(String.format(NAME_TO_UUID_URL, name))
                .doRequest()
                .asJsonObject();
        if (json == null) {
            return null;
        }

        String uuidWODashes = json.get("id").getAsString();
        String uuidWithDashes = UUID_REGEX_PATTERN.matcher(uuidWODashes).replaceAll("$1-$2-$3-$4-$5");

        return UUID.fromString(uuidWithDashes);
    }

    public static List<String> nameHistory(UUID uuid) {
        JsonArray array = HTTPRequest.GET(String.format(UUID_TO_NAME_HISTORY_URL, uuid.toString().replaceAll("-", "")))
                .doRequest()
                .asJson()
                .getAsJsonArray();
        List<String> names = new LinkedList<>();
        for (JsonElement el : array) {
            names.add(el.getAsJsonObject().get("name").getAsString());
        }

        return names;
    }

    public static Profile getProfile(UUID uuid) {
        JsonObject json = HTTPRequest.GET(String.format(UUID_TO_PROFILE, uuid.toString().replaceAll("-", "")))
                .doRequest()
                .asJsonObject();
        if (json == null) {
            return null;
        }

        Profile profile = new Profile();

        profile.name = json.get("name").getAsString();
        profile.uuid = UUID.fromString(UUID_REGEX_PATTERN.matcher(json.get("id").getAsString()).replaceAll("$1-$2-$3-$4-$5"));
        profile.hasMigrated = json.get("legacy") == null;

        return profile;
    }

    public static class Profile {
        public String name;
        public UUID uuid;
        public boolean hasMigrated;
    }
}
