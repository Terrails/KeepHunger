package terrails.statskeeper.config.configs;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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

    private static class CommentsOrDefaults {

        static String[] on_change_reset_defaults = {
                "MIN_HEALTH",
                "MAX_HEALTH",
                "STARTING_HEALTH"
        };
        static String[] health_thresholds_defaults = {
                "8 KEEP // Disables the health decreasing when the player is at or below this value, has to be the first (and lowest) and only one is allowed",
                "16 // Moves the lowest health from min to this value when achieved"
        };
        static String[] health_items_defaults = {
                "// toughasnails:lifeblood_crystal",
                "// cyclicmagic:heart_food",
                "// minecraft:wool;15 // metadata example, black wool",
                "minecraft:nether_star, 1 // amount example, 0,5 hearts instead of default 1"
        };
        static String health_items_comment = "Items that increase maximal health when used";
        static String health_message_comment = "Should the message for health removal and threshold achievement be shown to the player";
        static String enabled_comment = "In case that mods that modify health are present, make sure to disable them before using this";
        static String on_change_reset_comment = "Config options which should be considered for the reset of health, " +
                "\nall the available values are used by default";
        static String starting_health_comment = "Health with which the player should start with, values = CUSTOM(value), MIN, MAX";
        static String health_threshold_comment = "Values which, when achieved, move the lowest health of the player to the achieved value," +
                "\nit is also possible to make a non-decreasable threshold with 'KEEP' after the number";

    }

    public static void init(Configuration config, String category) {
        config.getStringList("Health Items", category, CommentsOrDefaults.health_items_defaults, CommentsOrDefaults.health_items_comment);
        enabled = config.get(category, "Enabled", true, CommentsOrDefaults.enabled_comment).getBoolean();
        min_health = config.get(category, "Min Health", 6, "", 0, Integer.MAX_VALUE).getInt();
        max_health = config.get(category, "Max Health", 20, "", 1, Integer.MAX_VALUE).getInt();
        health_decrease = config.get(category, "Health Reduction", 1, "",0, Integer.MAX_VALUE).getInt();
        starting_health = getStartingHealth(config, category);
        health_thresholds = getThresholds(config, category);
        on_change_reset = config.getStringList("On Change Reset", category, CommentsOrDefaults.on_change_reset_defaults, CommentsOrDefaults.on_change_reset_comment);
        health_message = config.get(category, "Health Message", true, CommentsOrDefaults.health_message_comment).getBoolean();
    }
    public static void postInit(Configuration config, String category) {
        String[] itemsArray = config.getStringList("Health Items", category, CommentsOrDefaults.health_items_defaults, CommentsOrDefaults.health_items_comment);
        health_items = new ArrayList<>();
        for (String string : itemsArray) {

            if (string.replaceAll("\\s+", "").startsWith("//"))
                continue;

            String itemString = string.contains(";") ? StringUtils.substringBefore(string, ";") : string.contains(",") ? StringUtils.substringBefore(string, ",") : string;
            Item item = Item.getByNameOrId(itemString.replaceAll("\\s+", ""));

            try {
                if (item == null || item == Items.AIR) {
                    throw new IllegalArgumentException("Non-existent item found '" + itemString + "', make sure to remove or comment the items which don't exist with '//'");
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                continue;
            }

            String metaString = string.contains(";") ? StringUtils.substringAfter(string ,";") : "-1";
            String metaString2 = metaString.contains(",") ? StringUtils.substringBefore(string, ",") : metaString;
            char minus_plus = metaString2.replaceAll("\\s+", "").charAt(0) == '-' ? '-' : '+';
            int meta = Integer.parseInt(minus_plus + metaString2.replaceAll("\\D+", ""));
            meta = meta < 0 ? OreDictionary.WILDCARD_VALUE : meta;

            String amountString = string.contains(",") ? StringUtils.substringAfter(string, ",") : "2";
            int amount = Integer.parseInt((amountString.contains("//") ? StringUtils.substringBefore(amountString, "//") : amountString).replaceAll("\\D+", ""));

            health_items.add(new HealthItem(item, meta, amount));
        }
    }

    private static int[] getThresholds(Configuration config, String category) {
        String[] thresholds = config.getStringList("Health Thresholds", category, CommentsOrDefaults.health_thresholds_defaults, CommentsOrDefaults.health_threshold_comment);

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
    private static int getStartingHealth(Configuration config, String category) {
        String string = config.get(category, "Starting Health", "MIN", CommentsOrDefaults.starting_health_comment).getString().toUpperCase();
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
        private int meta;
        private int amount;

        HealthItem(Item item, int meta, int amount) {
            this.item = item;
            this.meta = meta;
            this.amount = amount;
        }

        public Item getItem() {
            return this.item;
        }

        public int getMeta() {
            return this.meta;
        }

        public int getHealthAmount() {
            return this.amount;
        }
    }
}
