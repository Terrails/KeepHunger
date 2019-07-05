package terrails.statskeeper.config;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.*;

public class SKHealthConfig {

    public static boolean ENABLED;
    public static boolean HEALTH_MESSAGE;
    public static List<String> ON_CHANGE_RESET;

    public static int MAX_HEALTH;
    public static int MIN_HEALTH;
    public static int HEALTH_DECREASE;
    public static int STARTING_HEALTH;

    public static Map<ResourceLocation, Tuple<Integer, Boolean>> REGENERATIVE_ITEMS;
    public static NavigableSet<Integer> HEALTH_THRESHOLDS;

    static void init() {
        SKConfig.BUILDER.push("health");

        ConfigHandler.ENABLED = SKConfig.BUILDER.worldRestart().define("enabled", true);

        SKConfig.BUILDER.push("values");

        ConfigHandler.MAX_HEALTH = SKConfig.BUILDER
                .comment("The highest amount of health a player can have")
                .worldRestart()
                .defineInRange("maxHealthAmount", 20, 1, 1024);

        ConfigHandler.MIN_HEALTH = SKConfig.BUILDER
                .comment("The lowest amount of health a player can have. Can be set to 0 in case only maxHealthAmount is wanted")
                .worldRestart()
                .defineInRange("minHealthAmount", 6, 0, 1024);

        ConfigHandler.HEALTH_DECREASE = SKConfig.BUILDER
                .comment("The amount of health lost on each death. It will only work if minHealthAmount is higher than 0")
                .worldRestart()
                .defineInRange("deathDecreasedHealthAmount", 1, 0, 1024);

        ConfigHandler.STARTING_HEALTH = SKConfig.BUILDER
                .comment("The starting health for the player. Possible values are MIN, MAX or just a number")
                .worldRestart()
                .define("startingHealthAmount", "MIN");

        SKConfig.BUILDER.pop();

        SKConfig.BUILDER.push("additional");

        List<String> defaults = Arrays.asList("MIN_HEALTH", "MAX_HEALTH", "STARTING_HEALTH");
        ConfigHandler.ON_CHANGE_RESET = SKConfig.BUILDER
                .comment("Config options which should be considered for the reset of health. All available are used by default")
                .worldRestart()
                .defineList("configChangeReset", defaults, o -> o != null && String.class.isAssignableFrom(o.getClass()) && defaults.contains(o.toString().toUpperCase()));

        ConfigHandler.HEALTH_MESSAGE = SKConfig.BUILDER
                .comment("Show a message when a threshold is reached and when health is gained or lost")
                .define("healthChangeMessage", true);

        ConfigHandler.HEALTH_THRESHOLDS = SKConfig.BUILDER
                .comment("Values which, when achieved, move the lowest health of the player to the achieved value.\n" +
                        "The first threshold can also be non-removable, meaning that the health won't be removed till the player is over that threshold.\n" +
                        "This can only be used on the first threshold. To use it make the number negative. Make sure the values are in ascending order!")
                .worldRestart()
                .defineList("healthThresholds", Lists.newArrayList(-8, 16), o -> o != null && Integer.class.isAssignableFrom(o.getClass()));

        ConfigHandler.REGENERATIVE_ITEMS = SKConfig.BUILDER
                .comment("Items that increase health when used. Use a equal sign to define how much health is gained or lost.\n" +
                        "e.g. 'minecraft:apple = 1', the health gets increase by 0.5 hearts.\n" +
                        "Appending a ':' after the number will make the item which decreases health bypass thresholds")
                .worldRestart()
                .defineList("regenerativeItems", Lists.newArrayList("minecraft:nether_star = 1"), o -> o != null && String.class.isAssignableFrom(o.getClass()));

        SKConfig.BUILDER.pop(2);
    }
}