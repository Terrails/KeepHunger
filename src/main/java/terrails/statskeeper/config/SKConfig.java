package terrails.statskeeper.config;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SKConfig {

    public static SKConfig instance;

    public Hunger HUNGER_STATS = new Hunger();
    public Health HEALTH_STATS = new Health();
    public boolean keep_experience = true;
    public boolean drop_experience = false;

    public static class Hunger {

        public boolean keep_hunger = true;
        public int lowest_hunger = 6;

        public boolean keep_saturation = true;
        public int lowest_saturation = 2;

        public boolean show_effect_icon = true;
        public int no_appetite_time = 300;
    }

    public static class Health {

        public boolean enabled = true;
        public boolean min_health_start = true;
        public boolean on_change_reset = true;
        public boolean health_message = true;
        public boolean render_missing = true;

        public int max_health = 20;
        public int min_health = 6;
        public int health_decrease = 1;

        public List<HealthItem> health_items = Lists.newArrayList(new HealthItem(Items.NETHER_STAR, 2));

        public static class HealthItem {

            private Item item;
            private int amount;

            HealthItem(Item item, int amount) {
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

    public static void initialize() {
        File configFile = new File(FabricLoader.INSTANCE.getConfigDirectory(), "statskeeper.json");

        if (configFile.exists()) {
            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Health.HealthItem.class, new HealthItemJson()).create();
                instance = gson.fromJson(new FileReader(configFile), SKConfig.class);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        try {
            if (instance == null) instance = new SKConfig();
            FileWriter writer = new FileWriter(configFile);
            Gson gson = new GsonBuilder().registerTypeAdapter(Health.HealthItem.class, new HealthItemJson()).setPrettyPrinting().create();
            gson.toJson(instance, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
