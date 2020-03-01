package net.polarizedions.polarizedbot.modules.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.modules.*;
import net.polarizedions.polarizedbot.util.Colors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.literal;

@PolarizedBotModule
public class Variables implements IModule {
    private static Gson gson = new Gson();
    private static Type mapType = new TypeToken<Map<String, Long>>() {}.getType();

    private IMessageRunner runner = new Runner();
    private ICommand command = new Command();
    private VariableTracker variableTracker = new VariableTracker();
    private Pattern variablePattern;

    @Override
    public ICommand getCommand() {
        return this.command;
    }

    @Override
    public IMessageRunner getMessageRunner() {
        return this.runner;
    }

    @Override
    public void startup() {
        this.variableTracker.load();

        this.variablePattern = Pattern.compile("^([a-z0-9_]+)(\\+\\+|--|==)$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void shutdown() {
        this.variableTracker.save();
    }

    private class Command implements ICommand {
        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(literal("variables").executes(c -> this.run(c.getSource())));
            dispatcher.register(literal("vars").executes(c -> this.run(c.getSource())));
        }

        private int run(MessageSource source) {
            source.replyEmbed(spec -> {
                spec.setTitle("Variable Query");

                for (String var : variableTracker.variables.keySet()) {
                    spec.addField(var, variableTracker.query(var), true);
                }

                spec.setColor(Colors.INFO);
                spec.setTimestamp(Instant.now());
            });

            return 1;
        }

    }

    private class Runner implements IMessageRunner {
        @Override
        public void run(MessageSource source) {
            Matcher m = variablePattern.matcher(source.getMessage());
            if (m.matches()) {
                String var = m.group(1).replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
                String type = m.group(2);

                String value = "";
                if (type.equals("++")) {
                    value += variableTracker.increment(var);
                }
                else if (type.equals("--")) {
                    value += variableTracker.decrement(var);
                }
                else if (type.equals("==")) {
                    value += variableTracker.query(var);
                }
                else {
                    value += "unknown";
                }

                String finalValue = value; // because lambdas are silly
                source.replyEmbed(spec -> {
                    spec.addField(var, finalValue, true);

                    spec.setColor(Colors.INFO);
                    spec.setTimestamp(Instant.now());
                });
            }
        }
    }

    private class VariableTracker {
        public Map<String, Long> variables;

        public VariableTracker() {
            this.variables = new HashMap<>();
        }

        public void load() {
            try {
                this.variables = gson.fromJson(new FileReader("variables.json"), mapType);
            }
            catch (FileNotFoundException e) {
                Bot.logger.error("Error loading variables!", e);
            }
        }

        public void save() {
            try {
                FileWriter fw = new FileWriter("variables.json");
                fw.write(gson.toJson(this.variables));
                fw.flush();
                fw.close();
            }
            catch (IOException e) {
                Bot.logger.error("Error saving variables!", e);
            }
        }

        public String query(String var) {
            return this.variables.containsKey(var) ? "" +this.variables.get(var) : "undefined";
        }

        public Long increment(String var) {
            this.variables.put(var, this.variables.getOrDefault(var, 0L) + 1);
            return this.variables.get(var);
        }

        public Long decrement(String var) {
            this.variables.put(var, this.variables.getOrDefault(var, 0L) - 1);
            return this.variables.get(var);
        }
    }

}
