package terrails.keephunger.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.Int;

import java.io.File;

public class ConfigHandler{
    public static Configuration configFile;
    public static ConfigCategory general;
    public static boolean keepHunger;
    public static boolean keepXP;
    public static boolean noEating;
    public static boolean peacefulHunger;
    public static int noEatingTime;
    public static int minRespawn;
    public static boolean respawnMin;
    public static boolean saturation;

    public static void init(File file)
    {
        configFile = new Configuration(file);
        syncConfig();
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals("keep_hunger")) {
            syncConfig();
        }
    }

    public static void syncConfig()
    {
        String category = "General";
        configFile.addCustomCategoryComment(category, "General Settings");
        keepHunger = configFile.getBoolean("Hunger Saving", category, true, "Enable Hunger Saving");
        keepXP = configFile.getBoolean("XP Saving", category, true, "Enable Experience Saving");
        noEating = configFile.getBoolean("Eating Cooldown", category, true, "Enable Eating Cooldown");
        noEatingTime = configFile.getInt("Eating Cooldown (Seconds)", category, 300, 0, Int.MaxValue(), "Eating Cooldown (Seconds)");
        respawnMin = configFile.getBoolean("Respawn Minimal Food", category, true, "Respawn Minimal Food");
        minRespawn = configFile.getInt("Respawn Minimal Food Value", category, 8, 0, 20, "Respawn Minimal Food Value");
        saturation = configFile.getBoolean("Saturation Saving", category, true, "Enable Saturation Saving");

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }
}
