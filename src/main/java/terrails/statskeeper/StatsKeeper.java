package terrails.statskeeper;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.data.health.CapabilityHealth;
import terrails.statskeeper.event.*;
import terrails.statskeeper.potion.PotionNoAppetite;

@Mod(StatsKeeper.MOD_ID)
@EventBusSubscriber(bus = Bus.MOD)
public class StatsKeeper {

    public static final String MOD_ID = "statskeeper";
    public static final Logger LOGGER = LogManager.getLogger("Stats Keeper");

    public StatsKeeper() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SKConfig.CONFIG, "statskeeper.toml");
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.register(SKConfig.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityHealth.register();

        SKConfig.loadConfig(SKConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("statskeeper.toml"));

        MinecraftForge.EVENT_BUS.register(new BasicEventHandler());
        MinecraftForge.EVENT_BUS.register(new HungerEventHandler());
        MinecraftForge.EVENT_BUS.register(new HealthEventHandler());
    }

    @SubscribeEvent
    public static void registerPotions(final RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(new PotionNoAppetite());
    }
}
