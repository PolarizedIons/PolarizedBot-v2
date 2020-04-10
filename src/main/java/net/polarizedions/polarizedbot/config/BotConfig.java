package net.polarizedions.polarizedbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class BotConfig {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static BotConfig instance;

    public String discordToken = "";
    public String prefix = "!";
    public String lang = "en";
    public String wolframAlphaAPI = "";
    public String darkSkyAPI = "";
    public String mapBoxAPI = "";

    public static BotConfig get() {
        if (BotConfig.instance == null) {
            try {
                new File("config").mkdir();
                BotConfig.instance = gson.fromJson(new FileReader("config/config.json"), BotConfig.class);
            }
            catch (FileNotFoundException e) {
                try {
                    FileWriter fw = new FileWriter("config.json");
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
