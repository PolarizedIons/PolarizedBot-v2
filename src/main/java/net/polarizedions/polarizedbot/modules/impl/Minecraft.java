package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.api.MojangAPI;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;
import net.polarizedions.polarizedbot.modules.PolarizedBotModule;
import net.polarizedions.polarizedbot.util.Colors;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.argument;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.literal;

@PolarizedBotModule
public class Minecraft implements IModule {
    private static final String MINEOTAR_URL = "https://minotar.net/helm/%s/256.png";
    private ICommand command = new Command();

    @Override
    public ICommand getCommand() {
        return command;
    }

    private class Command implements ICommand {

        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(
                    literal("minecraft")
                    .then(
                            argument("name", greedyString()).executes(c -> this.minecraft(c.getSource(), getString(c, "name")))
                    )
            );
        }

        private int minecraft(MessageSource source, String name) {
            UUID uuid = MojangAPI.getPlayerUUID(name);

            if (uuid == null) {
                sendError(source, name);
            }
            else {
                List<String> nameHistoryList = MojangAPI.nameHistory(uuid);
                MojangAPI.Profile profile = MojangAPI.getProfile(uuid);

                if (nameHistoryList == null || profile == null) {
                    sendError(source, name);
                    return 1;
                }

                String nameHistory = String.join(", ", nameHistoryList);

                source.replyEmbed(spec -> {
                    spec.setTitle(Language.get("minecraft.title"));
                    spec.setThumbnail(String.format(MINEOTAR_URL, profile.name));
                    spec.setFooter("Image powered by mineotar.net", null);

                    spec.addField(Language.get("minecraft.name"), profile.name, true);
                    spec.addField(Language.get("minecraft.uuid"), profile.uuid.toString(), true);
                    spec.addField(Language.get("minecraft.name_history"), nameHistory, true);
                    spec.addField(Language.get("minecraft.migrated"), Language.get("minecraft.migrated." + profile.hasMigrated), true);

                    spec.setTimestamp(Instant.now());
                    spec.setColor(Colors.INFO);
                });
            }
            return 1;
        }

    }

    private void sendError(MessageSource source, String name) {
        source.replyEmbed(spec -> {
            spec.setTitle(Language.get("minecraft.title"));

            spec.addField(name, Language.get("minecraft.error"), true);

            spec.setTimestamp(Instant.now());
            spec.setColor(Colors.BAD);
        });
    }
}
