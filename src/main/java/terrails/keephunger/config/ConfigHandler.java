package terrails.keephunger.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;
import scala.Int;
import terrails.keephunger.Constants;

import java.io.File;

public class ConfigHandler
{
    public static Configuration configFile;

    public static final String GENERAL_SETTINGS = "General Settings";
    public static final String MINIMAL_SETTINGS = "Minimal Value Settings";
    public static final String TAN_SETTINGS = "Tough as Nails Settings";


    //Boolean

    public static boolean keepHunger;
    public static boolean keepXP;

    public static boolean noEating;

    public static boolean respawnMinHungerBoolean;

    public static boolean saturation;

    public static boolean thirst;
    public static boolean thirstBoolean;

    //Integer
    public static int noEatingTime;

    public static int minHungerValue;

    public static int thirstAmount;


    public static void init(File file)
    {
    configFile = new Configuration(file);
    syncConfig();

    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Constants.MODID)) {
            syncConfig();
        }
    }

    public static void syncConfig()
    {
        //Hunger
        keepHunger = configFile.getBoolean("Hunger Saving", GENERAL_SETTINGS, true, "Enable Hunger Saving");
        minHungerValue = configFile.getInt("Respawn Minimal Food Value", MINIMAL_SETTINGS, 8, 0, 20, "Respawn Minimal Food Value");
        respawnMinHungerBoolean = configFile.getBoolean("Respawn Minimal Food", MINIMAL_SETTINGS, true, "Respawn Minimal Food");

        //Saturation
        saturation = configFile.getBoolean("Saturation Saving", GENERAL_SETTINGS, true, "Enable Saturation Saving");

        //XP
        keepXP = configFile.getBoolean("XP Saving", GENERAL_SETTINGS, true, "Enable Experience Saving");

        //Eating Cooldown
        noEating = configFile.getBoolean("Eating Cooldown", GENERAL_SETTINGS, true, "Enable Eating Cooldown");
        noEatingTime = configFile.getInt("Eating Cooldown (Seconds)", GENERAL_SETTINGS, 300, 0, Int.MaxValue(), "Eating Cooldown (Seconds)");

        //ToughAsNails
        if(Loader.isModLoaded("toughasnails")){
            System.out.println("Keep Hunger TAN config loaded!");
            thirst = configFile.getBoolean("Thirst Saving", TAN_SETTINGS, true, "Enable Tough As Nails Thirst Saving");
            thirstBoolean = configFile.getBoolean("Respawn Minimal Thirst", TAN_SETTINGS, true, "Respawn Minimal Thirst");
            thirstAmount = configFile.getInt("Respawn Minimal Thirst Value", TAN_SETTINGS, 8, 0, 20, "Respawn Minimal Thirst Value");
        }
        else if(Loader.isModLoaded("ToughAsNails")){
            System.out.println("Keep Hunger TAN config loaded!");

            thirst = configFile.getBoolean("Thirst Saving", TAN_SETTINGS, true, "Enable Tough As Nails Thirst Saving");
            thirstBoolean = configFile.getBoolean("Respawn Minimal Thirst", TAN_SETTINGS, true, "Respawn Minimal Thirst");
            thirstAmount = configFile.getInt("Respawn Minimal Thirst Value", TAN_SETTINGS, 8, 0, 20, "Respawn Minimal Thirst Value");
        }
        if (configFile.hasChanged()) {
            configFile.save();
        }
    }
/*
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
        thirst = configFile.getBoolean("Thirst Saving", category, true, "Enable Tough As Nails Saturation Saving");
        amountThirst = configFile.getBoolean("Respawn Minimal Thirst", category, true, "Respawn Minimal Thirst");
        thirstAmount = configFile.getInt("Respawn Minimal Thirst Value", category, 8, 0, 20, "Respawn Minimal Thirst Value");

        if (configFile.hasChanged()) {
            configFile.save();
        }
    } */
}
