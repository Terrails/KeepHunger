package terrails.statskeeper.config;

import net.minecraft.item.Item;

import java.util.List;

public class SKHealthConfig {

    public static boolean enabled;
    public static boolean health_message;
    public static String[] on_change_reset;

    public static int max_health;
    public static int min_health;
    public static int health_decrease;

    public static int starting_health;
    public static int[] health_thresholds;
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

    private static int[] getThresholds(String[] thresholds) {
        int[] values = new int[thresholds.length];
        for (int i = 0; i < thresholds.length; i++) {
            if (i > 0 && thresholds[i].toUpperCase().contains("KEEP")) {
                throw new IllegalArgumentException("Only the first threshold can contain a KEEP argument '" + thresholds[i] + "'");
            }

            int value = Integer.parseInt(thresholds[i].replaceAll("[^0-9]+", ""));

            if (i > 0 && value < values[i - 1]) {
                throw new IllegalArgumentException("Thresholds have to be in ascending order!");
            }

            if (i == 0 && thresholds[i].toUpperCase().contains("KEEP")) {
                values[i] = -value;
                continue;
            }

            values[i] = value;
        }

        if (values.length > 0 && values[0] > 0 && values[0] <= starting_health) {
            throw new IllegalArgumentException("Threshold cannot be equal or smaller than starting health");
        }

        return values;
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

        public HealthItem(Item item, int amount) {
            this.item = item;
            this.amount = amount;
        }

        public Item getItem() {
            return this.item;
        }

        public int getHealthAmount() {
            return this.amount;
        }
    }
}
