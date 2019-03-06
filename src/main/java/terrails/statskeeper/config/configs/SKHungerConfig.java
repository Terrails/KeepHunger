package terrails.statskeeper.config.configs;

import net.minecraftforge.common.config.Configuration;

public class SKHungerConfig {
    
    public static boolean keep_hunger;
    public static int lowest_hunger;

    public static boolean keep_saturation;
    public static int lowest_saturation;

    public static int no_appetite_time;

    public static void init(Configuration config, String category) {
        keep_hunger = config.get(category, "Keep Hunger", true).getBoolean();
        lowest_hunger = config.get(category, "Lowest Hunger", 6, "", 0, 20).getInt();

        keep_saturation = config.get(category, "Keep Saturation", true).getBoolean();
        lowest_saturation = config.get(category, "Lowest Saturation", 2, "", 0, 20).getInt();

        no_appetite_time = config.get(category, "No Appetite Time (Seconds)", 300, "", 0, Integer.MAX_VALUE).getInt();
    }
}
