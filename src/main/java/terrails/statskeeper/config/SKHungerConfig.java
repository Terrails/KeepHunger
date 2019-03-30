package terrails.statskeeper.config;

public class SKHungerConfig {
    
    public static boolean KEEP_HUNGER;
    public static int LOWEST_HUNGER;

    public static boolean KEEP_SATURATION;
    public static boolean KEEP_SATURATION_WITH_HUNGER;
    public static int LOWEST_SATURATION;

    public static int NO_APPETITE_TIME;

    static void init() {
        SKConfig.BUILDER.push("hunger");

        ConfigHandler.KEEP_HUNGER = SKConfig.BUILDER
                .comment("Make the player keep hunger when respawning")
                .define("keepHunger", true);

        ConfigHandler.LOWEST_HUNGER = SKConfig.BUILDER
                .comment("The lowest hunger value the player can have when respawning, must be used with keepHunger")
                .defineInRange("lowestHunger", 6, 0, 20);

        SKConfig.BUILDER.push("saturation");

        ConfigHandler.KEEP_SATURATION = SKConfig.BUILDER
                .comment("Make the player keep saturation when respawning")
                .define("keepSaturation", true);

        ConfigHandler.LOWEST_SATURATION = SKConfig.BUILDER
                .comment("The lowest saturation value the player can have when respawning, must be used with keepSaturation")
                .defineInRange("lowestSaturation", 6, 0, 20);

        ConfigHandler.KEEP_SATURATION_WITH_HUNGER = SKConfig.BUILDER
                .comment("Make the player keep saturation when respawning only when hunger is full. Only usable with the other two options")
                .define("keepSaturationWithFullHunger", true);

        SKConfig.BUILDER.pop();

        SKConfig.BUILDER.push("no_appetite");

        ConfigHandler.NO_APPETITE_TIME = SKConfig.BUILDER
                .comment("The duration that the player will have the 'No Appetite' effect after respawning (seconds)")
                .defineInRange("effectDuration", 300, 0, Integer.MAX_VALUE);

        SKConfig.BUILDER.pop(2);
    }
}
