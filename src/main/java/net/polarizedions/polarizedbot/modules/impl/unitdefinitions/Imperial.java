package net.polarizedions.polarizedbot.modules.impl.unitdefinitions;

import net.polarizedions.polarizedbot.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Imperial {
    private static final Double FIVE_DIV_NINE = 5.0/9.0;

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Metric.MetricUnit> fromInch(Double from) {
        if (from < 40) { // < 100 cm
            return new Pair<>(from *  2.54, Metric.MetricUnit.CENTIMETER);
        }
        else { // > 100 cm
            return new Pair<>(from / 39.37, Metric.MetricUnit.METER);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Metric.MetricUnit> fromFoot(Double from) {
        if (from < 4) { // < 120 cm
            return new Pair<>(from * 30.48, Metric.MetricUnit.CENTIMETER);
        }
        else { // > 120 cm
            return new Pair<>(from / 3.281, Metric.MetricUnit.METER);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Metric.MetricUnit> fromYard(Double from) {
        if (from < 1.5) { // < 130 cm
            return new Pair<>(from * 91.44, Metric.MetricUnit.CENTIMETER);
        }
        else { // > 130 cm
            return new Pair<>(from / 1.094, Metric.MetricUnit.METER);
        }
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Metric.MetricUnit> fromMile(Double from) {
        return new Pair<>(from * 1.609, Metric.MetricUnit.KILOMETER);
    }

    @NotNull
    @Contract("_ -> new")
    public static Pair<Double, Metric.MetricUnit> fromFarenheit(Double from) {
        return new Pair<>((from - 32) * FIVE_DIV_NINE, Metric.MetricUnit.CELSIUS);
    }


    public enum ImperialUnit{
        INCH("in", "inch", "inches"),
        FOOT("ft", "ft", "foot", "feet"),
        YARD("yd", "yd", "yard", "yards"),
        MILE("mi", "mi", "mile", "miles"),

        FAHRENHEIT("F", "f", "°f"),
        ;

        public String unitName;
        public String[] unitDefinitions;

        @Contract(pure = true)
        ImperialUnit(String unitName, String... unitDefinitions) {
            this.unitName = unitName;
            this.unitDefinitions = unitDefinitions;
        }

    }
}
