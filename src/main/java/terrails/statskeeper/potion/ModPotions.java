package terrails.statskeeper.potion;

import com.google.common.collect.Lists;
import net.minecraft.potion.Potion;
import terrails.statskeeper.api.SKPotions;

import java.util.List;

public class ModPotions {

    public static List<Potion> potions = Lists.newArrayList();

    public static void init() {
        SKPotions.APPETITE = add(new NoAppetiteEffect("appetite"));
    }

    private static <T extends Potion> T add(T potion) {
        potions.add(potion);
        return potion;
    }
}