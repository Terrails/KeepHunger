package terrails.statskeeper.config;

import com.google.common.collect.Lists;
import net.minecraft.item.Items;
import java.util.List;

class SKConfigDummy {

    Hunger HUNGER_STATS = new Hunger();
    Health HEALTH_STATS = new Health();

    boolean keep_experience = true;
    boolean drop_experience = false;

    static class Hunger {

        boolean keep_hunger = true;
        int lowest_hunger = 6;

        boolean keep_saturation = true;
        int lowest_saturation = 2;

        boolean show_effect_icon = true;
        int no_appetite_time = 300;
    }
    static class Health {
        boolean enabled = true;
        boolean health_message = true;
        String[] on_change_reset = {"MIN_HEALTH", "MAX_HEALTH", "STARTING_HEALTH"};

        int max_health = 20;
        int min_health = 6;
        int health_decrease = 1;

        String starting_health = "MIN";
        String[] health_thresholds = {"8 KEEP", "16"};

        List<SKHealthConfig.HealthItem> health_items = Lists.newArrayList(new SKHealthConfig.HealthItem(Items.NETHER_STAR, 2));
    }

}
