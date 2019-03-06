package terrails.statskeeper.config.configs;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import terrails.statskeeper.StatsKeeper;

public class SKTANConfig {

    public static boolean keep_thirst;
    public static int lowest_thirst;

    public static boolean keep_hydration;
    public static int lowest_hydration;

    public static boolean keep_temperature;

    public static void init(Configuration config, String category) {
        if(Loader.isModLoaded("toughasnails")){
            keep_temperature = config.get(category, "Keep Temperature", true).getBoolean();

            keep_thirst = config.get(category, "Keep Thirst", true).getBoolean();
            lowest_thirst = config.get(category, "Lowest Thirst", 6, "", 0, 20).getInt();

            keep_hydration = config.get(category, "Keep Hydration", true).getBoolean();
            lowest_hydration = config.get(category, "Lowest Hydration", 2, "", 0, 20).getInt();

            StatsKeeper.LOGGER.info("ToughAsNails configuration loaded!");
        }
    }
}
