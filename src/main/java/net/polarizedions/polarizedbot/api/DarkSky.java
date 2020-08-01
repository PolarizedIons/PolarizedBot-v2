package net.polarizedions.polarizedbot.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import discord4j.core.spec.EmbedCreateSpec;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.api.apiutil.HTTPRequest;
import net.polarizedions.polarizedbot.config.BotConfig;
import net.polarizedions.polarizedbot.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DarkSky {
    public static final String FORECAST_URL = "https://api.darksky.net/forecast/%s/%s,%s?exclude=minutely,hourly,alerts,flags&lang=en&units=si";
    public static final double NINE_OVER_FIVE = 9.0 / 5.0;

    public static IWeatherResponse getWeather(Pair<Double, Double> location, ForecastType forecastType) throws DarkSkyException {
        String apiKey = BotConfig.get().darkSkyAPI;
        if (apiKey.isEmpty()) {
            throw new DarkSkyException("API Key missing!");
        }

        JsonObject json = HTTPRequest.GET(String.format(FORECAST_URL, apiKey, location.two, location.one))
                .doRequest()
                .asJsonObject();

        switch (forecastType) {
            case TODAY:
                return getDailyWeather(json, 0);
            case TOMORROW:
                return getDailyWeather(json, 1);
            case CURRENT:
            default:
                return getCurrentWeather(json);

        }
    }

    public static CurrentWeatherResponse getCurrentWeather(JsonObject data) {
        JsonObject forecast = data.getAsJsonObject("currently");
        JsonElement windBearing = forecast.get("windBearing");

        CurrentWeatherResponse response = new CurrentWeatherResponse();
        response.datetime = new Date(forecast.get("time").getAsLong() * 1000);
        response.summary = forecast.get("summary").getAsString();
        response.icon = WeatherIcon.fromDarkSky(forecast.get("icon").getAsString());
        response.precipitationProb = forecast.get("precipProbability").getAsDouble() * 100.00;
        response.temperatureC = forecast.get("temperature").getAsDouble();
        response.temperatureF = toDecimals((response.temperatureC * NINE_OVER_FIVE) + 32, 2);
        response.humidity = forecast.get("humidity").getAsDouble();
        response.pressure = forecast.get("pressure").getAsDouble();
        response.windSpeedMPS = forecast.get("windSpeed").getAsDouble();
        response.windSpeedMPH = toDecimals(response.windSpeedMPS * 2.23694, 2);
        response.windDirection = WindDirection.fromDarkSky(windBearing == null ? 0 : windBearing.getAsDouble());
        response.uvIndex = forecast.get("uvIndex").getAsInt();

        return response;
    }

    private static DailyWeatherResponse getDailyWeather(JsonObject data, int index) {
        JsonObject forecast = data.getAsJsonObject("daily").getAsJsonArray("data").get(index).getAsJsonObject();
        JsonElement windBearing = forecast.get("windBearing");

        DailyWeatherResponse response = new DailyWeatherResponse();
        response.datetime = new Date(forecast.get("time").getAsLong() * 1000);
        response.summary = forecast.get("summary").getAsString();
        response.offset = data.get("offset").getAsInt();
        response.icon = WeatherIcon.fromDarkSky(forecast.get("icon").getAsString());
        response.precipitationProb = forecast.get("precipProbability").getAsDouble() * 100.00;
        response.temperatureLowC = forecast.get("temperatureLow").getAsDouble();
        response.temperatureLowF = toDecimals((response.temperatureLowC * NINE_OVER_FIVE) + 32, 2);
        response.temperatureLowTime = new Date(forecast.get("temperatureLowTime").getAsLong() * 1000);
        response.temperatureHighC = forecast.get("temperatureHigh").getAsDouble();
        response.temperatureHighF = toDecimals((response.temperatureHighC * NINE_OVER_FIVE) + 32, 2);
        response.temperatureHighTime = new Date(forecast.get("temperatureHighTime").getAsLong() * 1000);
        response.sunriseTime = new Date(forecast.get("sunriseTime").getAsLong() * 1000);
        response.sunsetTime = new Date(forecast.get("sunsetTime").getAsLong() * 1000);
        response.humidity = forecast.get("humidity").getAsDouble();
        response.pressure = forecast.get("pressure").getAsDouble();
        response.windSpeedMPS = forecast.get("windSpeed").getAsDouble();
        response.windSpeedMPH = toDecimals(response.windSpeedMPS * 2.23694, 2);
        response.windDirection = WindDirection.fromDarkSky(windBearing == null ? 0 : windBearing.getAsDouble());
        response.uvIndex = forecast.get("uvIndex").getAsInt();

        return response;
    }

    private static double toDecimals(double in, int count) {
        double placesMask = Math.pow(10, count);
        return Math.floor(in * placesMask) / placesMask;
    }

    public enum ForecastType {
        CURRENT("current"),
        TODAY("today"),
        TOMORROW("tomorrow");

        public String inputStr;

        ForecastType(String inputStr) {
            this.inputStr = inputStr;
        }
    }

    public interface IWeatherResponse {
        void AddToEmbed(EmbedCreateSpec spec);
    }

    public static class CurrentWeatherResponse implements IWeatherResponse {
        public Date datetime;
        public String summary;
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
        public void AddToEmbed(EmbedCreateSpec spec) {
            spec.addField(Language.get("weather.summary.label"), icon.unicode + " " + summary, true);
            spec.addField(Language.get("weather.temperature.label"), Language.get("weather.temperature.value", temperatureC, temperatureF), true);
            spec.addField(Language.get("weather.precipitation.label"), Language.get("weather.precipitation.value", precipitationProb), true);
            spec.addField(Language.get("weather.humidity.label"), Language.get("weather.humidity.value", humidity), true);
            spec.addField(Language.get("weather.pressure.label"), Language.get("weather.pressure.value", pressure), true);
            spec.addField(Language.get("weather.wind_speed.label"), Language.get("weather.wind_speed.value", windSpeedMPS, windSpeedMPH), true);
            spec.addField(Language.get("weather.wind_direction.label"), Language.get("weather.wind_direction.value." + windDirection.code), true);
            spec.addField(Language.get("weather.uv_index.label"), Language.get("weather.uv_index.value", uvIndex), true);
            spec.setTimestamp(datetime.toInstant());
        }
    }

    public static class DailyWeatherResponse implements IWeatherResponse {
        public Date datetime;
        public String summary;
        public int offset;
        public WeatherIcon icon;
        public double precipitationProb;
        public double temperatureLowC;
        public double temperatureHighC;
        public Date temperatureLowTime;
        public double temperatureLowF;
        public double temperatureHighF;
        public Date temperatureHighTime;
        public double humidity;
        public double pressure;
        public double windSpeedMPS;
        public double windSpeedMPH;
        public WindDirection windDirection;
        public int uvIndex;
        public Date sunriseTime;
        public Date sunsetTime;

        @Override
        public void AddToEmbed(EmbedCreateSpec spec) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setTimeZone(TimeZone.getTimeZone("GMT" + (offset >= 0 ? "+" : "") + offset));
            String lowTime = timeFormat.format(temperatureLowTime);
            String highTime = timeFormat.format(temperatureHighTime);
            String sunrise = timeFormat.format(sunriseTime);
            String sunset = timeFormat.format(sunsetTime);

            spec.addField(Language.get("weather.summary.label"), icon.unicode + " " + summary, true);
            spec.addField(Language.get("weather.temperature_low.label"), Language.get("weather.temperature_low.value", temperatureLowC, temperatureLowF, lowTime), true);
            spec.addField(Language.get("weather.temperature_high.label"), Language.get("weather.temperature_high.value", temperatureHighC, temperatureHighF, highTime), true);
            spec.addField(Language.get("weather.sunrise_time.label"), Language.get("weather.sunrise_time.value", sunrise), true);
            spec.addField(Language.get("weather.sunset_time.label"), Language.get("weather.sunset_time.value", sunset), true);
            spec.addField(Language.get("weather.precipitation.label"), Language.get("weather.precipitation.value", precipitationProb), true);
            spec.addField(Language.get("weather.humidity.label"), Language.get("weather.humidity.value", humidity), true);
            spec.addField(Language.get("weather.pressure.label"), Language.get("weather.pressure.value", pressure), true);
            spec.addField(Language.get("weather.wind_speed.label"), Language.get("weather.wind_speed.value", windSpeedMPS, windSpeedMPH), true);
            spec.addField(Language.get("weather.wind_direction.label"), Language.get("weather.wind_direction.value." + windDirection.code), true);
            spec.addField(Language.get("weather.uv_index.label"), Language.get("weather.uv_index.value", uvIndex), true);
            spec.setTimestamp(datetime.toInstant());
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

        public static WeatherIcon fromDarkSky(String icon) {
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

        public static WindDirection fromDarkSky(double bearing) {
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
