package terrails.statskeeper.config.configs;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import terrails.statskeeper.StatsKeeper;

import java.util.*;
import java.util.function.Predicate;

public class SKHealthConfig {

    public static BooleanValue ENABLED;
    public static BooleanValue HEALTH_MESSAGE;
    public static ConfigValue<List<? extends String>> ON_CHANGE_RESET;

    public static IntValue MAX_HEALTH;
    public static IntValue MIN_HEALTH;
    public static IntValue HEALTH_DECREASE;

    public static Integer STARTING_HEALTH;
    public static Map<ResourceLocation, Integer> REGENERATIVE_ITEMS;
    public static ConfigValue<List<? extends Integer>> HEALTH_THRESHOLDS;

    private static ConfigValue<String> STARTING_HEALTH_TMP;
    private static ConfigValue<List<? extends String>> REGENERATIVE_ITEMS_TMP;

    public static void init(ForgeConfigSpec.Builder BUILDER) {
        BUILDER.push("health");

        ENABLED = BUILDER.worldRestart().define("enabled", true);

        BUILDER.push("values");

        MAX_HEALTH = BUILDER
                .comment("The highest amount of health a player can have")
                .worldRestart()
                .defineInRange("maxHealthAmount", 20, 1, 1024);

        MIN_HEALTH = BUILDER
                .comment("The lowest amount of health a player can have. Can be set to 0 in case only maxHealthAmount is wanted")
                .worldRestart()
                .defineInRange("minHealthAmount", 6, 0, 1024);

        HEALTH_DECREASE = BUILDER
                .comment("The amount of health lost on each death. It will only work if minHealthAmount is higher than 0")
                .worldRestart()
                .defineInRange("deathDecreasedHealthAmount", 1, 0, 1024);

        STARTING_HEALTH_TMP = BUILDER
                .comment("The starting health for the player. Possible values are MIN, MAX or just a number")
                .worldRestart()
                .define("startingHealthAmount", "MIN");

        BUILDER.pop();

        BUILDER.push("additional");

        List<String> resetDefaults = Arrays.asList("MIN_HEALTH", "MAX_HEALTH", "STARTING_HEALTH");
        ON_CHANGE_RESET = BUILDER
                .comment("Config options which should be considered for the reset of health. All available are used by default")
                .worldRestart()
                .defineList("configChangeReset", resetDefaults, o -> o != null && String.class.isAssignableFrom(o.getClass()) && resetDefaults.contains(o));

        HEALTH_MESSAGE = BUILDER
                .comment("Show a message when a threshold is reached and when health is gained or lost")
                .define("healthChangeMessage", true);

        HEALTH_THRESHOLDS = BUILDER
                .comment("Values which, when achieved, move the lowest health of the player to the achieved value.\n" +
                        "The first threshold can also be non-removable, meaning that the health won't be removed till the player is over that threshold.\n" +
                        "This can only be used on the first threshold. To use it make the number negative. Make sure the values are in ascending order!")
                .worldRestart()
                .defineList("healthThresholds", Lists.newArrayList(-8, 16), o -> o != null && Integer.class.isAssignableFrom(o.getClass()));

        REGENERATIVE_ITEMS_TMP = BUILDER
                .comment("Items that increase health when used. Use a equal sign to define how much health is gained or lost.\n" +
                        "e.g. 'minecraft:apple = 1', the health gets increase by 0.5 hearts")
                .worldRestart()
                .defineList("regenerativeItems", Lists.newArrayList("minecraft:nether_star = 1"), new HealthItems());

        BUILDER.pop(2);
    }


    public static void configReloading(ModConfig.ModConfigEvent event) {
        if (!event.getConfig().getModId().equals(StatsKeeper.MOD_ID)) {
            return;
        }

        String startingHealth = STARTING_HEALTH_TMP.get();
        if (!startingHealth.isEmpty()) {
            if (startingHealth.equals("MIN")) {
                STARTING_HEALTH = MIN_HEALTH.get();
            } else if (startingHealth.equals("MAX")) {
                STARTING_HEALTH = MAX_HEALTH.get();
            } else {
                int i = Integer.parseInt(startingHealth.replaceAll("[^0-9]", ""));
                if (i > MAX_HEALTH.get() || i < MIN_HEALTH.get()) throw new IllegalArgumentException("Starting health not in bounds!");
                STARTING_HEALTH = i;
            }

        } else {
            throw new IllegalArgumentException("Starting health was not set!");
        }

        REGENERATIVE_ITEMS = new HashMap<>();
        for (String string : REGENERATIVE_ITEMS_TMP.get()) {
            string = string.replaceAll("[\\s+]", "");

            String name = string.substring(0, string.indexOf("="));
            Integer amount = Integer.parseInt(string.substring(string.indexOf("=") + 1));

            REGENERATIVE_ITEMS.put(new ResourceLocation(name), amount);
        }

        List<? extends Integer> thresholds = HEALTH_THRESHOLDS.get();
        if (!thresholds.isEmpty()) {

            if (Math.abs(thresholds.get(0)) <= MIN_HEALTH.get()) {
                throw new IllegalArgumentException("Health threshold cannot be lower or equal to minHealth");
            }

            if (thresholds.get(thresholds.size() - 1) >= MAX_HEALTH.get()) {
                throw new IllegalArgumentException("Health threshold cannot be higher or equal to maxHealth");
            }
        }
    }

    private static class HealthItems implements Predicate<Object> {

        @Override
        public boolean test(Object o) {
            String string = (String) o;
            string = string.replaceAll("[\\s+]", "");

            if (!string.contains("=")) {
                StatsKeeper.LOGGER.error("Regenerative item '{}' is missing gained health amount. Skipping...", o);
                return false;
            }

            String name = string.substring(0, string.indexOf("="));
            int amount = Integer.parseInt(string.substring(string.indexOf("=") + 1));

            if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(name))) {
                StatsKeeper.LOGGER.warn("Regenerative Item '{}' could not be found in the item registry. Skipping ...", name);
                return false;
            }

            if (amount == 0) {
                StatsKeeper.LOGGER.error("Regenerative item '{}' cannot have gained/lost amount of 0. Skipping...", name);
                return false;
            }

            return true;
        }
    }
}
