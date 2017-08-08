package terrails.statskeeper.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import terrails.statskeeper.Constants;

public class ModPotions
    {
        public static Potion appetite;

    public static void init()
    {
        appetite = registerPotion("appetite", new NoAppetiteEffect(26).setPotionName("potion.appetite"));
    }

    public static Potion registerPotion(String name, Potion potion)
    {
        GameRegistry.register(potion, new ResourceLocation(Constants.MOD_ID, name));
        return potion;
    }

    public static PotionType registerPotionType(String name, PotionType potionType)
    {
        GameRegistry.register(potionType, new ResourceLocation(Constants.MOD_ID, name));
        return potionType;
    }
}