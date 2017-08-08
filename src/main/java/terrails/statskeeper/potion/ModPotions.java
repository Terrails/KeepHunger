package terrails.statskeeper.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import terrails.statskeeper.Constants;
import terrails.terracore.helper.Registry;

import static net.minecraft.potion.Potion.REGISTRY;
import static terrails.terracore.helper.Registry.registerPotion;

public class ModPotions {

    public static Potion appetite;
    private static int nextPotionTypeId;

    public static void init()
    {
        appetite = registerPotion("appetite", new NoAppetiteEffect(26, "appetite").setPotionName("potion.appetite"));
    }
}