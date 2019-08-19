package net.polarizedions.polarizedbot.util;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import net.polarizedions.polarizedbot.Bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class PresenceUpdator {
    private static final int RATE = 300; // seconds
    private List<Supplier<String>> presences;
    private DiscordClient client;
    private Timer timer;


    public PresenceUpdator(DiscordClient client) {
        this.client = client;
        this.presences = new ArrayList<>();
        this.timer = new Timer();

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PresenceUpdator.this.update();
            }
        }, 5000, RATE * 1000);

        presences.add(() -> "Napping...");
        presences.add(() -> "Playing fetch with " + this.getBotOwner());
        presences.add(() -> "Watching over " + this.getGuildCount() + " servers");
        presences.add(() -> "Downloading " + this.getRandomMember()  + "'s internet history");
        presences.add(() -> "Waking up Chell");
        presences.add(() -> "Fighting with GlaDOS for control");
        presences.add(() -> "Humming to himself");

    }

    private void update() {
        String newPresence = presences.get(ThreadLocalRandom.current().nextInt(presences.size())).get();
        this.client.updatePresence(Presence.online(Activity.playing(newPresence))).block();
    }

    private String getBotOwner() {
        User owner = this.client.getApplicationInfo().block().getOwner().block();
        return owner.getUsername() + "#" + owner.getDiscriminator();
    }

    private String getGuildCount() {
        return "" + this.client.getGuilds().count().block();
    }

    private String getRandomMember() {
        long guildCount = this.client.getGuilds().count().block();
        long guildI = ThreadLocalRandom.current().nextLong(guildCount);
        Guild guild = this.client.getGuilds()
                .skip(guildI)
                .next()
                .block();

        int membersCount = guild.getMemberCount().getAsInt();
        int membersI = ThreadLocalRandom.current().nextInt(membersCount);
        Member member = guild.getMembers()
                .skip(membersI)
                .next()
                .block();

        return member.getUsername() + "#" + member.getDiscriminator();
    }
}
