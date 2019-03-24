package terrails.statskeeper.config;

import com.google.common.collect.ImmutableSortedSet;
import net.minecraft.item.Item;

import java.util.*;

public class SKHealthConfig {

    public static boolean enabled;
    public static boolean health_message;
    public static String[] on_change_reset;

    public static int max_health;
    public static int min_health;
    public static int health_decrease;

    public static int starting_health;
    public static NavigableSet<Integer> health_thresholds;
    public static List<HealthItem> health_items;

    public static void init(SKConfigDummy instance) {
        enabled = instance.HEALTH_STATS.enabled;
        health_message = instance.HEALTH_STATS.health_message;
        on_change_reset = instance.HEALTH_STATS.on_change_reset;
        min_health = instance.HEALTH_STATS.min_health;
        max_health = instance.HEALTH_STATS.max_health;
        health_decrease = instance.HEALTH_STATS.health_decrease;
        starting_health = getStartingHealth(instance.HEALTH_STATS.starting_health);
        health_thresholds = getThresholds(instance.HEALTH_STATS.health_thresholds);

        health_items = instance.HEALTH_STATS.health_items;
    }

    private static NavigableSet<Integer> getThresholds(String[] strings) {
        TreeSet<Integer> thresholds = new TreeSet<>();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0 && strings[i].toUpperCase().contains("KEEP")) {
                throw new IllegalArgumentException("Only the first threshold can contain a KEEP argument '" + strings[i] + "'");
            }

            int value = Integer.parseInt(strings[i].replaceAll("[^0-9]+", ""));

            if (i == 0 && strings[i].toUpperCase().contains("KEEP")) {
                value = -value;
            }

            thresholds.add(value);
        }
        return ImmutableSortedSet.copyOf(thresholds);
    }
    private static int getStartingHealth(String string) {
        if (string.startsWith("CUSTOM")) {
            int i = Integer.parseInt(string.substring(string.indexOf("(") + 1, string.indexOf(")")));
            if (i > max_health || i < min_health ) throw new IllegalArgumentException("Starting health not in bounds!");
            return i;
        } else if (string.equals("MIN")) {
            return min_health;
        } else if (string.equals("MAX")) {
            return max_health;
        } else throw new NullPointerException("Starting health was not set!");
    }

    public static class HealthItem {

        private Item item;
        private int amount;
        private boolean bypass;

        public HealthItem(Item item, int amount, boolean bypass) {
            this.item = item;
            this.amount = amount;
            this.bypass = bypass;
        }

        public HealthItem(Item item, int amount) {
            this.item = item;
            this.amount = amount;
            this.bypass = false;
        }

        public Item getItem() {
            return this.item;
        }

        public int getHealthAmount() {
            return this.amount;
        }

        public boolean doesBypassThreshold() {
            return this.bypass;
        }
    }
}
