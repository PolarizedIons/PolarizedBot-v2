package net.polarizedions.polarizedbot.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.api.apiutil.HTTPRequest;
import net.polarizedions.polarizedbot.api.apiutil.WebHelper;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.util.Pair;

public class MapBox {
    public static final String LOCATION_API_URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/%s.json?access_token=%s";

    public static Location getLocation(String phrase) throws MapBoxException {
        String apiKey = BotConfig.get().mapBoxAPI;
        if (apiKey.isEmpty()) {
            throw new MapBox.MapBoxException("API Key missing!");
        }

        JsonObject json = HTTPRequest.GET(String.format(LOCATION_API_URL, WebHelper.encodeURIComponent(phrase), apiKey))
                .doRequest()
                .asJsonObject();

        JsonObject feature = json.getAsJsonArray("features").get(0).getAsJsonObject();
        JsonArray center = feature.getAsJsonArray("center");

        Location loc = new Location();
        loc.name = feature.get("place_name").getAsString();
        loc.coords = new Pair<>(center.get(0).getAsDouble(), center.get(1).getAsDouble());

        return loc;
    }

    public static class Location {
        public String name;
        public Pair<Double, Double> coords;
    }

    public static class MapBoxException extends Exception {
        public MapBoxException(String s) {
            super(s);
        }
    }
}
