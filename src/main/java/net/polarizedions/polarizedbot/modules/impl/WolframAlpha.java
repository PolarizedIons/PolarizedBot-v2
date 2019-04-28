package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.api.WolframAlphaAPI;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;
import net.polarizedions.polarizedbot.modules.PolarizedBotModule;
import net.polarizedions.polarizedbot.util.Colors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.argument;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.literal;

@PolarizedBotModule
public class WolframAlpha implements IModule {
    private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private ICommand command = new Command();

    @Override
    public ICommand getCommand() {
        return command;
    }

    private class Command implements ICommand {

        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(
                    literal("calc").then(
                            argument("q", greedyString())
                            .executes(c -> this.calc(c.getSource(), getString(c, "q")))
                    )
            );

            dispatcher.register(
                    literal("wolf").then(
                            argument("q", greedyString())
                                    .executes(c -> this.wolf(c.getSource(), getString(c, "q")))
                    )
            );
        }

        @Nullable
        private WolframAlphaAPI.WolframAlphaReply get(MessageSource source, String q) {
            WolframAlphaAPI.WolframAlphaReply data;
            try {
                data = WolframAlphaAPI.fetch(q);
            }
            catch (WolframAlphaAPI.WolframAlphaError ex) {
                source.replyEmbed(spec -> {
                    spec.setTitle(q);
                    spec.addField(Language.get("wolfram.errorField"), Language.get("wolfram.error." + ex.getMessage()), true);

                    spec.setColor(Colors.BAD);
                });

                return null;
            }

            if (! data.error.isEmpty()) {
                if (data.error.equals("no_data") && data.didYouMeans.size() > 0) {
                    StringBuilder resp = new StringBuilder("\n");
                    for (WolframAlphaAPI.DidYouMean dym : data.didYouMeans) {
                        resp.append(" - ").append(dym.value).append(" (**").append(DECIMAL_FORMAT.format(dym.chance)).append("%**)\n");
                    }

                    source.replyEmbed(spec -> {
                        spec.setTitle(q);
                        spec.addField(Language.get("wolfram.didYouMean"), resp.toString(), true);

                        spec.setColor(Colors.BAD);
                    });
                    return null;
                }
                source.replyEmbed(spec -> {
                    spec.setTitle(q);
                    spec.addField(Language.get("wolfram.errorField"), Language.get("wolfram.error." + data.error), true);

                    spec.setColor(Colors.BAD);
                });
                return null;
            }

            return data;
        }

        private int wolf(MessageSource source, String q) {
            WolframAlphaAPI.@Nullable WolframAlphaReply reply = this.get(source, q);
            if (reply == null) {
                return 1;
            }

            // < 2, because one of those is the input interpretation
            if (reply.pods.size() < 2) {
                source.replyEmbed(spec -> {
                    spec.setTitle(q);
                    spec.addField(Language.get("wolfram.errorField"), Language.get("wolfram.error.no_data"), true);

                    spec.setColor(Colors.BAD);
                });
                return 1;
            }

            this.reply(source, reply, reply.pods.size());

            return 1;
        }


        private int calc(MessageSource source, String q) {
            WolframAlphaAPI.@Nullable WolframAlphaReply reply = this.get(source, q);
            if (reply == null) {
                return 1;
            }

            // < 2, because one of those is the input interpretation
            if (reply.pods.size() < 2) {
                source.replyEmbed(spec -> {
                    spec.setTitle(q);
                    spec.addField(Language.get("wolfram.errorField"), Language.get("wolfram.error.no_data"), true);

                    spec.setColor(Colors.BAD);
                });
                return 1;
            }

            this.reply(source, reply, Math.min(2, reply.pods.size()));
            return 1;
        }

        private void reply(MessageSource source, WolframAlphaAPI.WolframAlphaReply wolfData, int count) {
            source.replyEmbed(spec -> {
                spec.setColor(Colors.INFO);

                for (int i = 0; i < count; i++) {
                    WolframAlphaAPI.Pod pod = wolfData.pods.get(i);

                    String title = this.discordEscape(pod.name);
                    String value = this.discordEscape(String.join("\n- ", pod.data));
                    if (value.contains("\n")) {
                        value = "- " + value;
                    }

                    spec.addField(title, value, false);
                }
            });
        }

        @NotNull
        @Contract(pure = true)
        private String discordEscape(@NotNull String text) {
            return text.replaceAll("([*_~])", "\\\\$1");
        }
    }
}
