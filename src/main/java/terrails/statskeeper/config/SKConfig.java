package terrails.statskeeper.config;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import terrails.statskeeper.StatsKeeper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SKConfig {

    public static Configuration configFile;

    static final String BASIC = "Basic";
    static final String HUNGER = "Hunger";
    static final String HEALTH = "Health";

    static final String MOD_COMP = "Mod-Compatibility";
    static final String TOUGH_AS_NAILS = MOD_COMP + "." + "ToughAsNails";

    public static boolean keep_experience;
    public static boolean drop_experience;

    public static class Hunger {

        public static boolean keep_hunger;
        public static int lowest_hunger;

        public static boolean keep_saturation;
        public static int lowest_saturation;

        public static int no_appetite_time;
    }

    public static class Health {

        public static boolean enabled;
        public static boolean min_health_start;
        public static boolean on_change_reset;
        public static boolean health_message;

        public static int max_health;
        public static int min_health;
        public static int health_decrease;

        public static List<HealthItem> health_items;

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

    public static class Compatibility {

        public static class TAN {

            public static boolean keep_thirst;
            public static int lowest_thirst;

            public static boolean keep_hydration;
            public static int lowest_hydration;

            public static boolean keep_temperature;
        }
    }

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(StatsKeeper.MOD_ID)) {
            SKConfig.syncConfig();
        }
    }

    public static void initialize(File directory) {
        configFile = new Configuration(new File(directory, StatsKeeper.MOD_ID + ".cfg"));
        MinecraftForge.EVENT_BUS.register(new SKConfig());
        syncConfig();
    }

    public static void syncConfig(){
        keep_experience = configFile.get(BASIC, "Keep Experience", true).getBoolean();
        drop_experience = configFile.get(BASIC, "Drop Experience", false).getBoolean();
        syncHunger();
        syncHealth();
        syncModComp();

        if (configFile.hasChanged()) {
            configFile.save();
        }
    }

    private static void syncHunger() {
        Hunger.keep_hunger = configFile.get(HUNGER, "Keep Hunger", true).getBoolean();
        Hunger.lowest_hunger = configFile.get(HUNGER, "Lowest Hunger", 6, "", 0, 20).getInt();

        Hunger.keep_saturation = configFile.get(HUNGER, "Keep Saturation", true).getBoolean();
        Hunger.lowest_saturation = configFile.get(HUNGER, "Lowest Saturation", 2, "", 0, 20).getInt();

        Hunger.no_appetite_time = configFile.get(HUNGER, "No Appetite Time (Seconds)", 300, "", 0, Integer.MAX_VALUE).getInt();
    }
    private static void syncHealth() {
        String[] itemsArray = configFile.getStringList("Health Items", HEALTH, new String[]{
                "// toughasnails:lifeblood_crystal", "// cyclicmagic:heart_food", "// minecraft:wool;15 // metadata example, black wool",
                "minecraft:nether_star, 4 // amount example, 2 hearts instead of default 1"}, "items that will add max health on right click");
        Health.health_items = new ArrayList<>();
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

            Health.health_items.add(new Health.HealthItem(item, meta, amount));
        }

        Health.enabled = configFile.get(HEALTH, "Enabled", true, "If using mods that mess with health system like ToughAsNails" +
                "\nmake sure to disable theirs in config files before using this health system").getBoolean();
        Health.min_health_start = configFile.get(HEALTH, "Min Health Start", true).getBoolean();
        Health.min_health = configFile.get(HEALTH, "Min Health", 6, "", 0, Integer.MAX_VALUE).getInt();
        Health.max_health = configFile.get(HEALTH, "Max Health", 20, "", 0, Integer.MAX_VALUE).getInt();
        Health.health_decrease = configFile.get(HEALTH, "Health Reduction", 1, "",0, Integer.MAX_VALUE).getInt();
        Health.on_change_reset = configFile.get(HEALTH, "On Change Reset", true, "Reset the health of each player on config change").getBoolean();
        Health.health_message = configFile.get(HEALTH, "Health Message", true, "Should the message for health removal be shown to the player").getBoolean();
    }
    private static void syncModComp() {
        if(Loader.isModLoaded("toughasnails")){
            Compatibility.TAN.keep_temperature = configFile.get(TOUGH_AS_NAILS, "Keep Temperature", true).getBoolean();

            Compatibility.TAN.keep_thirst = configFile.get(TOUGH_AS_NAILS, "Keep Thirst", true).getBoolean();
            Compatibility.TAN.lowest_thirst = configFile.get(TOUGH_AS_NAILS, "Lowest Thirst", 6, "", 0, 20).getInt();

            Compatibility.TAN.keep_hydration = configFile.get(TOUGH_AS_NAILS, "Keep Hydration", true).getBoolean();
            Compatibility.TAN.lowest_hydration = configFile.get(TOUGH_AS_NAILS, "Lowest Hydration", 2, "", 0, 20).getInt();

            StatsKeeper.LOGGER.info("ToughAsNails configuration loaded!");
        }
    }

}
