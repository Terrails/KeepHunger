package terrails.statskeeper.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.Constants;
import terrails.statskeeper.StatsKeeper;

import java.io.File;

@Mod.EventBusSubscriber
public class ConfigHandler
{
    public static Configuration configFile;

    public static final String GENERAL_SETTINGS = "General Settings";
    public static final String MINIMAL_SETTINGS = GENERAL_SETTINGS + "." + "Value/Timing Settings";
    public static final String HEALTH_SETTINGS = GENERAL_SETTINGS + "." + "Health Settings";
    public static final String TAN_SETTINGS = "Tough as Nails Settings";


    public static String[] itemNameArray;

    //Boolean
    public static boolean healthSystem;
    public static boolean startWithMinHealth;
    public static boolean healthMessage;

    public static boolean keepHunger;
    public static boolean keepXP;
    public static boolean keepThirst;
    public static boolean keepSaturation;
    public static boolean keepHydration;
    public static boolean keepTemperature;

    public static boolean dropXP;
    public static boolean noEating;


    //Integer
    public static int minHungerAmount;
    public static int minThirstAmount;
    public static float minSaturationAmount;
    public static float minHydrationAmount;

    public static int maxHealth;
    public static int minHealth;
    public static int removedHealthOnDeath;

    public static int noEatingTime;


    public static void init(File directory) {
        configFile = new Configuration(new File(directory, StatsKeeper.MOD_ID + ".cfg"));
        syncConfig();
    }

    @SubscribeEvent
    public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(StatsKeeper.MOD_ID)) {
            syncConfig();
        }
    }

    private static String[] ITEM_DEFAULT(){
        return new String[]{"toughasnails:lifeblood_crystal", "cyclicmagic:heart_food"};
    }

    public static void syncConfig(){
        String[] array = configFile.getStringList("Health Items", HEALTH_SETTINGS, ITEM_DEFAULT(), "items that will add max health on right click");
        itemNameArray = array;

        syncConfigLanguage();
        syncFoodStatsConfig();
        syncExpStatsConfig();
        syncHealthConfig();
        syncToughAsNailsConfig();

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    public static void syncConfigLanguage(){
        configFile.setCategoryLanguageKey(MINIMAL_SETTINGS, "config.category.minimalSettings.title");
        configFile.setCategoryLanguageKey(HEALTH_SETTINGS, "config.category.healthSettings.title").setCategoryComment(HEALTH_SETTINGS,
                "If using mods that mess with health system like " +
                        "ToughAsNails\nmake sure to disable theirs in config files before using this health system");
    }

    public static void syncFoodStatsConfig(){

        //Hunger
        keepHunger = configFile.get(GENERAL_SETTINGS, "Keep Hunger", true).getBoolean();
        minHungerAmount = configFile.get(MINIMAL_SETTINGS, "Minimal Hunger Amount", 8, "", 0, 20).getInt();

        //Saturation
        keepSaturation = configFile.get(GENERAL_SETTINGS, "Keep Saturation", false).getBoolean();
        minSaturationAmount = configFile.get(MINIMAL_SETTINGS, "Minimal Saturation Amount", 0, "", 0, 20).getInt();

        //Eating Cooldown
        noEating = configFile.get(GENERAL_SETTINGS, "No Appetite Effect", true).getBoolean();
        noEatingTime = configFile.get(MINIMAL_SETTINGS, "No Appetite Time (Seconds)", 300, "", 0, Integer.MAX_VALUE).getInt();
    }

    public static void syncExpStatsConfig(){
        //Experience
        keepXP = configFile.get(GENERAL_SETTINGS, "Keep Experience", true).getBoolean();
        dropXP = configFile.get(GENERAL_SETTINGS, "Drop Experience on Death", false).getBoolean();
    }

    public static void syncHealthConfig(){

        //Health
        healthSystem = configFile.get(HEALTH_SETTINGS, "Health System", false).getBoolean();
        startWithMinHealth = configFile.get(HEALTH_SETTINGS, "Start with Min Health", false).getBoolean();
        minHealth = configFile.get(HEALTH_SETTINGS, "Min Health", 0, "", 0, Integer.MAX_VALUE).getInt();
        maxHealth = configFile.get(HEALTH_SETTINGS, "Max Health", 20, "", 0, Integer.MAX_VALUE).getInt();
        removedHealthOnDeath = configFile.get(HEALTH_SETTINGS, "Reduced Health on Death", 0, "",0, Integer.MAX_VALUE).getInt();
        healthMessage = configFile.get(HEALTH_SETTINGS, "Health Message", true).getBoolean();
    }

    public static void syncToughAsNailsConfig(){
        //ToughAsNails
        if(Loader.isModLoaded("toughasnails") || Loader.isModLoaded("ToughAsNails")){
            Constants.LOGGER.info("TAN addon activated!");

            //Temperature
            keepTemperature = configFile.get(TAN_SETTINGS, "Keep Temperature", true).getBoolean();

            //Thirst
            keepThirst = configFile.get(TAN_SETTINGS, "Keep Thirst", true).getBoolean();
            minThirstAmount = configFile.get(TAN_SETTINGS, "Minimal Thirst Amount", 8, "", 0, 20).getInt();

            //Hydration
            keepHydration = configFile.get(TAN_SETTINGS, "Keep Hydration", false).getBoolean();
            minHydrationAmount = configFile.get(TAN_SETTINGS, "Minimal Hydration Amount", 0, "", 0, 20).getInt();

        }
    }


}
