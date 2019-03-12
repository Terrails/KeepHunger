package terrails.statskeeper.config.configs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;

public class SKHungerConfig {
    
    public static BooleanValue KEEP_HUNGER;
    public static IntValue LOWEST_HUNGER;

    public static BooleanValue KEEP_SATURATION;
    public static BooleanValue KEEP_SATURATION_WITH_HUNGER;
    public static IntValue LOWEST_SATURATION;

    public static IntValue NO_APPETITE_TIME;

    public static void init(ForgeConfigSpec.Builder BUILDER) {
        BUILDER.push("hunger");

        KEEP_HUNGER = BUILDER
                .comment("Make the player keep hunger when respawning")
                .define("keepHunger", true);

        LOWEST_HUNGER = BUILDER
                .comment("The lowest hunger value the player can have when respawning, must be used with keepHunger")
                .defineInRange("lowestHunger", 6, 0, 20);

        BUILDER.push("saturation");

        KEEP_SATURATION = BUILDER
                .comment("Make the player keep saturation when respawning")
                .define("keepSaturation", true);

        LOWEST_SATURATION = BUILDER
                .comment("The lowest saturation value the player can have when respawning, must be used with keepSaturation")
                .defineInRange("lowestSaturation", 6, 0, 20);

        KEEP_SATURATION_WITH_HUNGER = BUILDER
                .comment("Make the player keep saturation when respawning only when hunger is full. Only usable with the other two options")
                .define("keepSaturationWithFullHunger", true);

        BUILDER.pop();

        BUILDER.push("no_appetite");

        NO_APPETITE_TIME = BUILDER
                .comment("The duration that the player will have the 'No Appetite' effect after respawning (seconds)")
                .defineInRange("effectDuration", 300, 0, Integer.MAX_VALUE);

        BUILDER.pop(2);
    }
}
