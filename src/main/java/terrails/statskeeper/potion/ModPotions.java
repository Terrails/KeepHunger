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
        appetite = registerPotion("appetite", new NoAppetiteEffect(26).setPotionName("potion.appetite"));
    }

    public static Potion registerPotion(String name, Potion potion)
    {
        GameRegistry.register(potion, new ResourceLocation("stats_keeper", name));
        return potion;
    }

    public static PotionType registerPotionType(String name, PotionType potionType)
    {
        GameRegistry.register(potionType, new ResourceLocation("stats_keeper", name));
        return potionType;
    }
}