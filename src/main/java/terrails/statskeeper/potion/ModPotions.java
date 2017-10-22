package terrails.statskeeper.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class ModPotions {
    
    public static Potion appetite;

    public ModPotions()
    {
    }

    public static void init()
    {
        appetite = registerPotion("appetite", new NoAppetiteEffect("appetite"));
    }

    public static Potion registerPotion(String name, Potion potion)
    {
        GameRegistry.register(potion);
        return potion;
    }
}