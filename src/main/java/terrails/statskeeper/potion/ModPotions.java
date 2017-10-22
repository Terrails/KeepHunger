package terrails.statskeeper.potion;

import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameRegistry;
import terrails.terracore.registry.PotionRegistry;

public class ModPotions extends PotionRegistry {
    
    public static Potion APPETITE;

    public static void init() {
        APPETITE = addPotion(new NoAppetiteEffect("appetite"));
        GameRegistry.register(APPETITE);
    }
}