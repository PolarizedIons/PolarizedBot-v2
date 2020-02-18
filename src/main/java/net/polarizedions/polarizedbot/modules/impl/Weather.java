package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
                    literal("w").then(
                            argument("l", greedyString())
                            .executes(c -> this.weather(c.getSource(), getString(c, "l")))
                    )
            );
            
            dispatcher.register(
                    literal("weather").then(
                            argument("l", greedyString())
                            .executes(c -> this.weather(c.getSource(), getString(c, "l")))
                    )
            );
        }

        private int weather(MessageSource source, String location) {
            MapBox.Location loc;
            DarkSky.WeatherResponse weather;
            try {
                loc = MapBox.getLocation(location);
            } catch (MapBox.MapBoxException e) {
                source.replyEmbed(spec -> {
                    spec.setTitle(Language.get("weather.error_title"));
                    spec.addField(Language.get("weather.error"), Language.get("weather.error_location", location), false);

                    spec.setColor(Colors.BAD);
                    spec.setTimestamp(Instant.now());
                });
                return 1;
            }

            try {
                weather = DarkSky.getWeather(loc.coords);
            } catch (DarkSky.DarkSkyException e) {
                source.replyEmbed(spec -> {
                    spec.setTitle(Language.get("weather.error_title"));
                    spec.addField(Language.get("weather.error"), Language.get("weather.error_weather", loc.name), false);

                    spec.setColor(Colors.BAD);
                    spec.setTimestamp(Instant.now());
                });
                return 1;
            }

            source.replyEmbed(spec -> {
                spec.setTitle(Language.get("weather.title", loc.name));

                spec.addField(Language.get("weather.summery.label"), weather.icon.unicode + " " + weather.summery, true);
                spec.addField(Language.get("weather.precipitation.label"), Language.get("weather.precipitation.value", weather.precipitationProb), true);
                spec.addField(Language.get("weather.temperature_c.label"), Language.get("weather.temperature_c.value", weather.temperatureC), true);
                spec.addField(Language.get("weather.temperature_f.label"), Language.get("weather.temperature_f.value", weather.temperatureF), true);
                spec.addField(Language.get("weather.humidity.label"), Language.get("weather.humidity.value", weather.humidity), true);
                spec.addField(Language.get("weather.pressure.label"), Language.get("weather.pressure.value", weather.pressure), true);
                spec.addField(Language.get("weather.wind_speed_mps.label"), Language.get("weather.wind_speed_mps.value", weather.windSpeedMPS), true);
                spec.addField(Language.get("weather.wind_speed_mph.label"), Language.get("weather.wind_speed_mph.value", weather.windSpeedMPH), true);
                spec.addField(Language.get("weather.wind_direction.label"), Language.get("weather.wind_direction.value", weather.windDirection.code), true);
                spec.addField(Language.get("weather.uv_index.label"), Language.get("weather.uv_index.value", weather.uvIndex), true);

                spec.setColor(Colors.INFO);
                spec.setFooter("Powered by MapBox.com & DarkSky.net", null);
                spec.setTimestamp(weather.datetime.toInstant());
            });
            return 1;
        }
    }
}
