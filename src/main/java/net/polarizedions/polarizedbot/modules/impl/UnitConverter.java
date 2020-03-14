package net.polarizedions.polarizedbot.modules.impl;

import net.polarizedions.polarizedbot.modules.IMessageRunner;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;
import net.polarizedions.polarizedbot.modules.PolarizedBotModule;
import net.polarizedions.polarizedbot.modules.impl.unitdefinitions.Imperial;
import net.polarizedions.polarizedbot.modules.impl.unitdefinitions.Metric;
import net.polarizedions.polarizedbot.util.Pair;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@PolarizedBotModule
public class UnitConverter implements IModule {
    IMessageRunner runner = new Runner();

    @Override
    public IMessageRunner getMessageRunner() {
        return runner;
    }

    private class Runner implements IMessageRunner {
        private final String REGEX_TEMPLATE = "(?:^|\\s)(-?[0-9]+(?:[.,][0-9]+)?)\\s?(%s)(?:$|\\W)";
        private final DecimalFormat FORMAT = new DecimalFormat("#.##");
        private Map<String, Imperial.ImperialUnit> imperialMap = new HashMap<>();
        private Map<String, Metric.MetricUnit> metricMap = new HashMap<>();
        private Pattern imperialRegex;
        private Pattern metricRegex;

        private Runner() {
            StringBuilder imperialDefinitionsBuilder = new StringBuilder();
            for (Imperial.ImperialUnit unit : Imperial.ImperialUnit.values()) {
                for (String definition : unit.unitDefinitions) {
                    imperialMap.put(definition, unit);
                    imperialDefinitionsBuilder.append("|").append(definition);
                }
            }
            String imperialDefinitions = imperialDefinitionsBuilder.substring(1);
            imperialRegex = Pattern.compile(String.format(REGEX_TEMPLATE, imperialDefinitions), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);


            StringBuilder metricDefinitionsBuilder = new StringBuilder();
            for (Metric.MetricUnit unit : Metric.MetricUnit.values()) {
                for (String definition : unit.unitDefinitions) {
                    metricMap.put(definition, unit);
                    metricDefinitionsBuilder.append("|").append(definition);
                }
            }
            String metricDefintions = metricDefinitionsBuilder.substring(1);
            metricRegex = Pattern.compile(String.format(REGEX_TEMPLATE, metricDefintions), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        }

        @Override
        public void run(MessageSource source) {
            StringBuilder response = new StringBuilder("```");

            Matcher imperialMatcher = imperialRegex.matcher(source.getMessage());
            Matcher metricMatcher = metricRegex.matcher(source.getMessage());

            while (imperialMatcher.find()) {
                Imperial.ImperialUnit unit = imperialMap.get(imperialMatcher.group(2).toLowerCase());
                Double amount = Double.parseDouble(imperialMatcher.group(1).replaceAll(",", "."));
                response.append(FORMAT.format(amount)).append(" ").append(unit.unitName).append(" -> ").append(convert(amount, unit)).append("\n");
            }

            while (metricMatcher.find()) {
                Metric.MetricUnit unit = metricMap.get(metricMatcher.group(2).toLowerCase());
                Double amount = Double.parseDouble(metricMatcher.group(1).replaceAll(",", "."));
                response.append(FORMAT.format(amount)).append(" ").append(unit.unitName).append(" -> ").append(convert(amount, unit)).append("\n");
            }

            if (response.length() > 3) {
                source.reply(response.append("```").toString());
            }
        }

        String convert(Double in, Imperial.ImperialUnit unit) {
            Pair<Double, Metric.MetricUnit> converted = convertImperial(in, unit);
            return FORMAT.format(converted.one) + " " + converted.two.unitName;
        }

        String convert(Double in, Metric.MetricUnit unit) {
            Pair<Double, Imperial.ImperialUnit> converted = convertMetric(in, unit);
            return FORMAT.format(converted.one) + " " + converted.two.unitName;
        }

        private Pair<Double, Metric.MetricUnit> convertImperial(Double in, Imperial.ImperialUnit unit) {
            switch (unit) {
                case FOOT:
                    return Imperial.fromFoot(in);
                case INCH:
                    return Imperial.fromInch(in);
                case YARD:
                    return Imperial.fromYard(in);
                case MILE:
                    return Imperial.fromMile(in);
                case FAHRENHEIT:
                    return Imperial.fromFarenheit(in);
            }
            return null;
        }
    }

    private Pair<Double, Imperial.ImperialUnit> convertMetric(Double in, Metric.MetricUnit unit) {
        switch (unit) {
            case CENTIMETER:
                return Metric.fromCentimeter(in);
            case METER:
                return Metric.fromMeter(in);
            case KILOMETER:
                return Metric.fromKilometer(in);
            case CELSIUS:
                return Metric.fromCelcius(in);
        }
        return null;
    }
}
