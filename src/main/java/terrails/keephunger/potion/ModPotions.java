package terrails.keephunger.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModPotions
    {
        public static Potion appetite;

    public static void init()
    {
        appetite = registerPotion("appetite", new NoAppetiteEffect(26).setPotionName("potion.appetite"));
    }

    public static Potion registerPotion(String name, Potion potion)
    {
        GameRegistry.register(potion, new ResourceLocation("keep_hunger", name));
        return potion;
    }

    public static PotionType registerPotionType(String name, PotionType potionType)
    {
        GameRegistry.register(potionType, new ResourceLocation("keep_hunger", name));
        return potionType;
    }
}