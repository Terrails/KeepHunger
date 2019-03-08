package terrails.statskeeper.config;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.config.configs.SKHealthConfig;
import terrails.statskeeper.config.configs.SKHungerConfig;
import terrails.statskeeper.config.configs.SKTANConfig;

import java.io.File;

public class SKConfig {

    static Configuration configuration;

    static class Categories {
        static final String BASIC = "Basic";
        static final String HUNGER = "Hunger";
        static final String HEALTH = "Health";

        static final String MOD_COMP = "Mod-Compatibility";
        static final String TOUGH_AS_NAILS = MOD_COMP + "." + "ToughAsNails";
    }

    public static boolean keep_experience;
    public static boolean drop_experience;

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(StatsKeeper.MOD_ID)) {
            SKConfig.syncConfig();
            SKConfig.syncPostConfig();
        }
    }

    public static void initialize(File directory) {
        configuration = new Configuration(new File(directory, StatsKeeper.MOD_ID + ".cfg"));
        MinecraftForge.EVENT_BUS.register(new SKConfig());
        syncConfig();
    }

    private static void syncConfig(){
        keep_experience = configuration.get(Categories.BASIC, "Keep Experience", true).getBoolean();
        drop_experience = configuration.get(Categories.BASIC, "Drop Experience", false).getBoolean();

        SKHealthConfig.init(configuration, Categories.HEALTH);
        SKHungerConfig.init(configuration, Categories.HUNGER);
        SKTANConfig.init(configuration, Categories.TOUGH_AS_NAILS);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static void syncPostConfig() {
        SKHealthConfig.postInit(configuration, Categories.HEALTH);
    }
}
