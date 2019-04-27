package net.polarizedions.polarizedbot.util;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import net.polarizedions.polarizedbot.Bot;
import net.polarizedions.polarizedbot.modules.MessageSource;
import org.jetbrains.annotations.NotNull;

public class PermUtil {
    /**
     * Whether the user is the application owner
     *
     * @param bot the bot instance
     * @param user the user to check
     * @return if they are the bot owner
     */
    public static boolean userIsOwner(@NotNull Bot bot, User user) {
        return bot.getClient().getApplicationInfo().block().getOwnerId().asLong() == user.getId().asLong();
    }

    /**
     * Whether the user has admin permissions for the bot. Requires to be application owner or have the "Manage Server"
     * permission.
     *
     * @param bot the bot instance
     * @param member the user to check
     * @return if they have admin permissions
     */
    public static boolean userIsAdmin(Bot bot, @NotNull Member member) {
        return member.getBasePermissions().block().contains(Permission.MANAGE_GUILD) || userIsOwner(bot, member);
    }

    /**
     * Whether the author of the message has admin permissions
     *
     * @param source the message
     * @return if the author is an admin
     */
    public static boolean userIsAdmin(@NotNull MessageSource source) {
        return source.isPrivateMessage() ? PermUtil.userIsOwner(source.getBot(), source.getUser().get()) : PermUtil.userIsAdmin(source.getBot(), source.getUser().get().asMember(source.getGuildId()).block());
    }
}
