package net.polarizedions.polarizedbot.modules.impl;

import com.mojang.brigadier.CommandDispatcher;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.Language;
import net.polarizedions.polarizedbot.modules.ICommand;
import net.polarizedions.polarizedbot.modules.IModule;
import net.polarizedions.polarizedbot.modules.MessageSource;
import net.polarizedions.polarizedbot.modules.ModuleManager;
import net.polarizedions.polarizedbot.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.argument;
import static net.polarizedions.polarizedbot.modules.brigadier.BrigadierTypeFixer.literal;
import static net.polarizedions.polarizedbot.modules.brigadier.DiscordPing.discordPing;
import static net.polarizedions.polarizedbot.modules.brigadier.DiscordPing.getDiscordPing;

public class About implements IModule {
    private ICommand command = new Command();

    @Override
    public ICommand getCommand() {
        return command;
    }

    private class Command implements ICommand {

        @Override
        public void register(CommandDispatcher<MessageSource> dispatcher) {
            dispatcher.register(
                    literal("about").executes(c -> this.about(c.getSource()))
            );

            dispatcher.register(
                    literal("info")
                            .then(
                                    argument("person", discordPing())
                                            .executes(c -> this.info(c.getSource(), getDiscordPing(c, "person")))
                            )
                        .executes(c -> this.info(c.getSource(), null))
            );
        }

        int about(@NotNull MessageSource source) {
            source.replyEmbed(spec -> {
                Bot bot = source.getBot();
                ModuleManager moduleManager = bot.getModuleManager();
                User ourUser = source.getClient().getSelf().block();


                spec.setTitle(Language.get("about.title"));
                spec.addField(Language.get("about.name"), ourUser.getUsername() + "#" + ourUser.getDiscriminator(), true);
                spec.addField(Language.get("about.moduleCount"), Language.get("about.moduleCountValue", moduleManager.getActiveModuleCount(), moduleManager.getModuleCount()), true);
                spec.addField(Language.get("about.version"), "todo", true);
                spec.addField(Language.get("about.uptime"), "todo", true);


                spec.setThumbnail(ourUser.getAvatarUrl());
                spec.setColor(Colors.INFO);
                spec.setTimestamp(Instant.now());
            });

            return 1;
        }

        int info(MessageSource source, Long discordId) {
            DiscordClient client = source.getClient();
            final User user = discordId == null ? source.getUser().get() : client.getUserById(Snowflake.of(discordId)).block();

            source.replyEmbed(spec -> {
                spec.setTitle(Language.get("info.title"));
                spec.addField(Language.get("info.userName"), user.getUsername() + "#" + user.getDiscriminator(), true);
                spec.addField(Language.get("info.userId"), user.getId().asLong() + "", true);

                spec.setThumbnail(user.getAvatarUrl());
                spec.setColor(Colors.INFO);
                spec.setTimestamp(Instant.now());
            });

            return 1;
        }
    }
}
