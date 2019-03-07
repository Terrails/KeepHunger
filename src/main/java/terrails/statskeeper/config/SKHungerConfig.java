package terrails.statskeeper.config;

public class SKHungerConfig {

    public static boolean keep_hunger;
    public static int lowest_hunger;

    public static boolean keep_saturation;
    public static int lowest_saturation;

    public static int no_appetite_time;
    public static boolean show_effect_icon;

    public static void init(SKConfigDummy instance) {
        keep_hunger = instance.HUNGER_STATS.keep_hunger;
        lowest_hunger = instance.HUNGER_STATS.lowest_hunger;

        keep_saturation = instance.HUNGER_STATS.keep_saturation;
        lowest_saturation = instance.HUNGER_STATS.lowest_saturation;

        no_appetite_time = instance.HUNGER_STATS.no_appetite_time;
        show_effect_icon = instance.HUNGER_STATS.show_effect_icon;
    }
}
