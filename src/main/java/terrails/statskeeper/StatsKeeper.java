package terrails.statskeeper;

import net.minecraft.potion.Effect;
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
import terrails.statskeeper.api.SKEffects;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.health.HealthCapability;
import terrails.statskeeper.event.*;
import terrails.statskeeper.effect.NoAppetiteEffect;

import java.util.UUID;

@Mod(StatsKeeper.MOD_ID)
@EventBusSubscriber(bus = Bus.MOD)
public class StatsKeeper {

    public static final UUID HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    public static final Logger LOGGER = LogManager.getLogger("Stats Keeper");
    public static final String MOD_ID = "statskeeper";

    public StatsKeeper() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SKConfig.CONFIG, "statskeeper.toml");
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.register(SKConfig.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        HealthCapability.register();

        SKConfig.loadConfig(SKConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("statskeeper.toml"));

        MinecraftForge.EVENT_BUS.register(new BasicStatHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHealthHandler());
    }

    @SubscribeEvent
    public static void registerEffects(final RegistryEvent.Register<Effect> event) {
        SKEffects.NO_APPETITE = new NoAppetiteEffect();
        event.getRegistry().register(SKEffects.NO_APPETITE);
    }
}
