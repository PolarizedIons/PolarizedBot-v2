package net.polarizedions.polarizedbot.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.polarizedions.polarizedbot.api.apiutil.HTTPRequest;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.util.Pair;

import java.util.Date;

public class DarkSky {
    public static final String FORECAST_URL = "https://api.darksky.net/forecast/%s/%s,%s?exclude=minutely,hourly,daily,alerts,flags&lang=en&units=si";
    public static final double NINE_OVER_FIVE = 9.0 / 5.0;

    public static WeatherResponse getWeather(Pair<Double, Double> location) throws DarkSkyException {
        String apiKey = BotConfig.get().darkSkyAPI;
        if (apiKey.isEmpty()) {
            throw new DarkSkyException("API Key missing!");
        }

        JsonObject json = HTTPRequest.GET(String.format(FORECAST_URL, apiKey, location.two, location.one))
                .doRequest()
                .asJsonObject();

        JsonObject currently = json.getAsJsonObject("currently");
        JsonElement windBearing = currently.get("windBearing");
        WeatherResponse response = new WeatherResponse();
        response.datetime = new Date(currently.get("time").getAsLong() * 1000);
        response.summery = currently.get("summary").getAsString();
        response.icon = WeatherIcon.fromDarkBox(currently.get("icon").getAsString());
        response.precipitationProb = currently.get("precipProbability").getAsDouble() * 100.00;
        response.temperatureC = currently.get("temperature").getAsDouble();
        response.temperatureF = toDecimals((response.temperatureC * NINE_OVER_FIVE) + 32, 2);
        response.humidity = currently.get("humidity").getAsDouble();
        response.pressure = currently.get("pressure").getAsDouble();
        response.windSpeedMPS = currently.get("windSpeed").getAsDouble();
        response.windSpeedMPH = toDecimals(response.windSpeedMPS * 2.23694, 2);
        response.windDirection = WindDirection.fromDarkBox(windBearing == null ? 0 : windBearing.getAsDouble());
        response.uvIndex = currently.get("uvIndex").getAsInt();

        return response;
    }

    private static double toDecimals(double in, int count) {
        double placesMask = Math.pow(10, count);
        return Math.floor(in * placesMask) / placesMask;
    }

    public static class WeatherResponse {
        public Date datetime;
        public String summery;
        public WeatherIcon icon;
        public double precipitationProb;
        public double temperatureC;
        public double temperatureF;
        public double humidity;
        public double pressure;
        public double windSpeedMPS;
        public double windSpeedMPH;
        public WindDirection windDirection;
        public int uvIndex;

        @Override
        public String toString() {
            return "WeatherResponse{" +
                    "datetime=" + datetime +
                    ", summery='" + summery + '\'' +
                    ", icon=" + icon +
                    ", precipitationProb=" + precipitationProb +
                    ", temperatureC=" + temperatureC +
                    ", temperatureF=" + temperatureF +
                    ", humidity=" + humidity +
                    ", pressure=" + pressure +
                    ", windSpeedMPS=" + windSpeedMPS +
                    ", windSpeedMPH=" + windSpeedMPH +
                    ", windDirection=" + windDirection +
                    ", uvIndex=" + uvIndex +
                    '}';
        }
    }

    public enum WeatherIcon {
        CLEAR_DAY("\uD83C\uDF1E"),
        CLEAR_NIGHT("\uD83C\uDF1D"),
        RAIN("\uD83C\uDF27"),
        SNOW("\uD83C\uDF28"),
        WIND("\uD83D\uDCA8"),
        FOG("\uD83C\uDF2B"),
        CLOUDY("☁"),
        PARTLY_CLOUDY("⛅"),
        THUNDERSTORM("\uD83C\uDF29"),
        TORNADO("\uD83C\uDF2A"),
        UNKNOWN("\uD83D\uDEAB");

        public String unicode;

        WeatherIcon(String unicode) {
            this.unicode = unicode;
        }

        public static WeatherIcon fromDarkBox(String icon) {
            switch (icon) {
                case "clear-day":
                    return CLEAR_DAY;
                case "clear-night":
                    return CLEAR_NIGHT;
                case "rain":
                    return RAIN;
                case "snow":
                case "sleet":
                case "hail":
                    return SNOW;
                case "wind":
                    return WIND;
                case "fog":
                    return FOG;
                case "cloudy":
                    return CLOUDY;
                case "partly-cloudy-day":
                case "partly-cloudy-night":
                    return PARTLY_CLOUDY;
                case "thunderstorm":
                    return THUNDERSTORM;
                case "tornado":
                    return TORNADO;
                default:
                    return UNKNOWN;
            }

        }
    }

    public enum WindDirection {
        NORTH("N"),
        NORTH_EAST("NE"),
        EAST("E"),
        SOUTH_EAST("SE"),
        SOUTH("S"),
        SOUTH_WEST("SW"),
        WEST("W"),
        NORTH_WEST("NW");

        public String code;
        WindDirection(String code) {
            this.code = code;
        }

        public static WindDirection fromDarkBox(double bearing) {
            bearing = bearing + 180 % 360;

            if (bearing >= 337.5 || bearing < 22.5) {
                return NORTH;
            }
            else if (bearing >= 22.5 && bearing < 67.5) {
                return NORTH_EAST;
            }
            else if (bearing >= 67.5 && bearing < 112.5) {
                return EAST;
            }
            else if (bearing >= 112.5 && bearing < 157.5) {
                return SOUTH_EAST;
            }
            else if (bearing >= 157.5 && bearing < 202.5) {
                return SOUTH;
            }
            else if (bearing >= 202.5 && bearing < 247.5) {
                return SOUTH_WEST;
            }
            else if (bearing >= 247.5 && bearing < 292.5) {
                return WEST;
            }
            else if (bearing >= 292.5 && bearing < 337.5) {
                return NORTH_WEST;
            }

            return NORTH;
        }
    }

    public static class DarkSkyException extends Exception {
        public DarkSkyException(String s) {
            super(s);
        }
    }
}
