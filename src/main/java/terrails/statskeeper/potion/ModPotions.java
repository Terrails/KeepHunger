package terrails.statskeeper.potion;

import com.google.common.collect.Lists;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber
public class ModPotions {

    private static List<Potion> potions = Lists.newArrayList();

    public static Potion APPETITE;

    public static void init() {
        APPETITE = add(new NoAppetiteEffect("appetite"));
    }

    public static <T extends Potion> T add(T potion) {
        potions.add(potion);
        return potion;
    }

    public static Potion[] get() {
        return potions.toArray(new Potion[potions.size()]);
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().registerAll(get());
    }
}