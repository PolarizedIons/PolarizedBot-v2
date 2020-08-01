package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.api.DarkSky;
import net.polarizedions.polarizedbot.api.MapBox;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;
import net.polarizedions.polarizedbot.modules.PolarizedBotModule;
import net.polarizedions.polarizedbot.util.Colors;

import java.time.Instant;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.argument;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.literal;

@PolarizedBotModule
public class Weather implements IModule {
    private ICommand command = new Weather.Command();

    @Override
    public ICommand getCommand() {
        return command;
    }

    private class Command implements ICommand {
        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(
                    literal("w")
                        .then(
                                argument("loc", greedyString())
                                .executes(c -> this.weather(c.getSource(), getString(c, "loc")))
                        )
            );
            
            dispatcher.register(
                    literal("weather")
                        .then(
                                argument("loc", greedyString())
                                .executes(c -> this.weather(c.getSource(), getString(c, "loc")))
                        )
            );
        }


        private int weather(MessageSource source, String location) {
            DarkSky.ForecastType forecastType = DarkSky.ForecastType.CURRENT;
            for (DarkSky.ForecastType type : DarkSky.ForecastType.values()) {
                if (location.startsWith(type.inputStr)) {
                    location = location.substring(type.inputStr.length());
                    forecastType = type;
                }
            }

            MapBox.Location loc;
            DarkSky.IWeatherResponse weather;
            try {
                loc = MapBox.getLocation(location);
            } catch (MapBox.MapBoxException e) {
                String finalLocation = location;
                source.replyEmbed(spec -> {
                    spec.setTitle(Language.get("weather.error_title"));
                    spec.addField(Language.get("weather.error"), Language.get("weather.error_location", finalLocation), false);

                    spec.setColor(Colors.BAD);
                    spec.setTimestamp(Instant.now());
                });
                return 1;
            }

            try {
                weather = DarkSky.getWeather(loc.coords, forecastType);
            } catch (DarkSky.DarkSkyException e) {
                source.replyEmbed(spec -> {
                    spec.setTitle(Language.get("weather.error_title"));
                    spec.addField(Language.get("weather.error"), Language.get("weather.error_weather", loc.name), false);

                    spec.setColor(Colors.BAD);
                    spec.setTimestamp(Instant.now());
                });
                return 1;
            }

            DarkSky.ForecastType finalForecastType = forecastType;
            source.replyEmbed(spec -> {
                spec.setTitle(Language.get("weather.title." + finalForecastType.inputStr, loc.name));

                weather.AddToEmbed(spec);

                spec.setColor(Colors.INFO);
                spec.setFooter("Powered by MapBox.com & DarkSky.net", null);
            });
            return 1;
        }
    }
}
