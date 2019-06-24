package net.polarizedions.polarizedbot.modules.impl.unitdefinitions;

import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Metric {
    private static final Double NINE_DIV_FIVE = 9.0/5.0;


    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Imperial.ImperialUnit> fromMillimeter(Double from) {
        if (from < 305) { // < 12 in
            return new Pair<>(from / 25.4, Imperial.ImperialUnit.INCH);
        }
        else { // > 12 in
            return new Pair<>(from / 304.8, Imperial.ImperialUnit.FOOT);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Imperial.ImperialUnit> fromCentimeter(Double from) {
        if (from < 31) { // < 1 ft
            return new Pair<>(from / 2.54, Imperial.ImperialUnit.INCH);
        }
        else if (from < 92) { // < 1 yd
            return new Pair<>(from / 30.48, Imperial.ImperialUnit.FOOT);
        }
        else { // > 1 yd
            return new Pair<>(from / 91.44, Imperial.ImperialUnit.YARD);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Imperial.ImperialUnit> fromMeter(Double from) {
        return new Pair<>(from * 1.094, Imperial.ImperialUnit.YARD);
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Imperial.ImperialUnit> fromKilometer(Double from) {
        return new Pair<>(from / 1.609 , Imperial.ImperialUnit.MILE);
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Imperial.ImperialUnit> fromCelcius(Double from) {
        return new Pair<>((from * NINE_DIV_FIVE) + 32, Imperial.ImperialUnit.FAHRENHEIT);
    }

    public enum MetricUnit {
        CENTIMETER("cm", "cm", "centimeter", "centimeters"),
        METER("m", "m", "meter", "meters"),
        KILOMETER("km", "km", "kilometer", "kilometers"),

        CELSIUS("C", "c", "°c"),
        ;

        public String unitName;
        public String[] unitDefinitions;

        @Contract(pure = true)
        MetricUnit(String unitName, String... unitDefinitions) {
            this.unitName = unitName;
            this.unitDefinitions = unitDefinitions;
        }
    }
}
