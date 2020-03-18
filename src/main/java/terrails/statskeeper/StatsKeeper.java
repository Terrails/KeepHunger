package terrails.statskeeper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.potion.Effect;
import net.minecraftforge.common.ForgeConfigSpec;
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
import terrails.statskeeper.capabilities.HealthCapability;
import terrails.statskeeper.effect.NoAppetiteEffect;
import terrails.statskeeper.feature.*;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

@Mod(StatsKeeper.MOD_ID)
@EventBusSubscriber(bus = Bus.MOD)
public class StatsKeeper {

    public static final UUID HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    public static final Logger LOGGER = LogManager.getLogger("Stats Keeper");
    public static final String MOD_ID = "statskeeper";

    private static final ForgeConfigSpec CONFIG_SPEC;
    private static final Feature[] FEATURES = {
            new ExperienceFeature(),
            new HungerFeature(),
            new HealthFeature()
    };

    public StatsKeeper() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC, "statskeeper.toml");
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        HealthCapability.register();
        loadConfig(FMLPaths.CONFIGDIR.get().resolve("statskeeper.toml"));
    }

    @SubscribeEvent
    public static void registerEffects(final RegistryEvent.Register<Effect> event) {
        SKEffects.NO_APPETITE = new NoAppetiteEffect();
        event.getRegistry().register(SKEffects.NO_APPETITE);
    }

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        Arrays.stream(FEATURES).filter(Feature::canLoad).forEach(feature -> {
            builder.push(feature.name());
            feature.setupConfig(builder);
            builder.pop();
        });
        CONFIG_SPEC = builder.build();
    }

    private static void loadConfig(Path path) {
        StatsKeeper.LOGGER.debug("Loading config file {}", path);

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        StatsKeeper.LOGGER.debug("Built TOML config for {}", path.toString());
        configData.load();
        StatsKeeper.LOGGER.debug("Loaded TOML config file {}", path.toString());
        CONFIG_SPEC.setConfig(configData);
        Arrays.stream(FEATURES).filter(Feature::canLoad).forEach(Feature::registerEventBus);
    }

    @SubscribeEvent
    public static void configLoading(final ModConfig.ModConfigEvent event) {
        if (!event.getConfig().getModId().equals(StatsKeeper.MOD_ID))
            return;

        Arrays.stream(FEATURES).filter(Feature::canLoad).forEach(Feature::configLoad);
        StatsKeeper.LOGGER.debug("Loaded {} config file {}", StatsKeeper.MOD_ID, event.getConfig().getFileName());
    }
}
