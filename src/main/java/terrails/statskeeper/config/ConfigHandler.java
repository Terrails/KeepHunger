package terrails.statskeeper.config;

import com.google.common.collect.ImmutableSortedSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.registries.ForgeRegistries;
import terrails.statskeeper.StatsKeeper;

import java.util.*;

class ConfigHandler {

    // General
    static BooleanValue KEEP_EXPERIENCE;
    static BooleanValue DROP_EXPERIENCE;

    // Hunger
    static BooleanValue KEEP_HUNGER;
    static IntValue LOWEST_HUNGER;

    static BooleanValue KEEP_SATURATION;
    static BooleanValue KEEP_SATURATION_WITH_HUNGER;
    static IntValue LOWEST_SATURATION;

    static IntValue NO_APPETITE_TIME;

    // Health
    static BooleanValue ENABLED;
    static BooleanValue HEALTH_MESSAGE;
    static ConfigValue<List<? extends String>> ON_CHANGE_RESET;

    static IntValue MAX_HEALTH;
    static IntValue MIN_HEALTH;
    static IntValue HEALTH_DECREASE;

    static ConfigValue<List<? extends Integer>> HEALTH_THRESHOLDS;
    static ConfigValue<List<? extends String>> REGENERATIVE_ITEMS;
    static ConfigValue<String> STARTING_HEALTH;

    static void configLoading() {
        // General
        SKConfig.KEEP_EXPERIENCE = KEEP_EXPERIENCE.get();
        SKConfig.DROP_EXPERIENCE = DROP_EXPERIENCE.get();

        // Hunger
        SKHungerConfig.KEEP_HUNGER = KEEP_HUNGER.get();
        SKHungerConfig.LOWEST_HUNGER = LOWEST_HUNGER.get();

        SKHungerConfig.KEEP_SATURATION = KEEP_SATURATION.get();
        SKHungerConfig.KEEP_SATURATION_WITH_HUNGER = KEEP_SATURATION_WITH_HUNGER.get();
        SKHungerConfig.LOWEST_SATURATION = LOWEST_SATURATION.get();

        SKHungerConfig.NO_APPETITE_TIME = NO_APPETITE_TIME.get();

        // Health
        SKHealthConfig.ENABLED = ENABLED.get();
        SKHealthConfig.HEALTH_MESSAGE = HEALTH_MESSAGE.get();
        SKHealthConfig.ON_CHANGE_RESET = new ArrayList<>(ON_CHANGE_RESET.get());

        SKHealthConfig.MAX_HEALTH = MAX_HEALTH.get();
        SKHealthConfig.MIN_HEALTH = MIN_HEALTH.get();
        SKHealthConfig.HEALTH_DECREASE = HEALTH_DECREASE.get();
        SKHealthConfig.HEALTH_THRESHOLDS = ImmutableSortedSet.copyOf(HEALTH_THRESHOLDS.get());

        SKHealthConfig.REGENERATIVE_ITEMS = new HashMap<>();
        for (String string : REGENERATIVE_ITEMS.get()) {
            string = string.replaceAll("[\\s+]", "");

            String name = string.substring(0, string.indexOf("="));

            if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(name))) {
                StatsKeeper.LOGGER.error("Regenerative Item '{}' could not be found in the item registry. Skipping...", name);
                continue;
            }

            int amount = Integer.parseInt(string.substring(string.indexOf("=") + 1, (string.endsWith(":") ? string.lastIndexOf(":") : string.length())));

            if (amount == 0) {
                StatsKeeper.LOGGER.error("Regenerative Item '{}' cannot have health set to 0. Skipping...", name);
                continue;
            }

            boolean bypass = string.endsWith(":");

            if (bypass && amount > 0) {
                StatsKeeper.LOGGER.error("Regenerative Item '{}' cannot bypass thresholds when it gains health. Skipping...", name);
                continue;
            }

            SKHealthConfig.REGENERATIVE_ITEMS.put(new ResourceLocation(name), new Tuple<>(amount, bypass));
        }

        switch (STARTING_HEALTH.get()) {
            case "MIN":
                SKHealthConfig.STARTING_HEALTH = MIN_HEALTH.get();
                break;
            case "MAX":
                SKHealthConfig.STARTING_HEALTH = MAX_HEALTH.get();
                break;
            default:
                int i = Integer.parseInt(STARTING_HEALTH.get().replaceAll("[^0-9]", ""));
                if (i > MAX_HEALTH.get() || i < MIN_HEALTH.get()) {
                    StatsKeeper.LOGGER.error("Starting health '{}' is out of bounds! Using default value...", i);
                    SKHealthConfig.STARTING_HEALTH = MIN_HEALTH.get();
                }
                break;
        }


    }
}
