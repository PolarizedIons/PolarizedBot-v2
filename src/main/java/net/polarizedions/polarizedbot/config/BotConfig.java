package net.polarizedions.polarizedbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BotConfig {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static BotConfig instance;

    public String discordToken = "";
    public String prefix = "!";
    public String lang = "en";

    public static BotConfig get() {
        if (BotConfig.instance == null) {
            try {
                BotConfig.instance = gson.fromJson(new FileReader("botConfig.json"), BotConfig.class);
            }
            catch (FileNotFoundException e) {
                try {
                    FileWriter fw = new FileWriter("botConfig.json");
                    fw.write(gson.toJson(new BotConfig()));
                    fw.close();
                }
                catch (IOException e1) {
                    throw new RuntimeException("Unable to save config file!");
                }

                throw new RuntimeException("Unable to load config file! Creating one.");
            }
        }

        return BotConfig.instance;
    }
}
