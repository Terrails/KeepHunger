package terrails.statskeeper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.data.health.CapabilityHealth;
import terrails.statskeeper.event.*;
import terrails.statskeeper.packet.StatsMessageTAN;
import terrails.statskeeper.potion.ModPotions;
import terrails.terracore.base.MainModClass;
import terrails.terracore.base.proxies.ProxyBase;
import terrails.terracore.base.registry.RegistryList;
import terrails.terracore.base.registry.RegistryType;

@Mod(modid = StatsKeeper.MOD_ID,
        name = StatsKeeper.MOD_NAME,
        version = StatsKeeper.VERSION,
        guiFactory = StatsKeeper.GUI_FACTORY,
        dependencies = "required-after:terracore@[0.0.0,);")
public class StatsKeeper extends MainModClass<StatsKeeper> {

    public static final String MOD_ID = "stats_keeper";
    public static final String MOD_NAME = "Stats Keeper";
    public static final String VERSION = "@VERSION@";
    public static final String GUI_FACTORY = "terrails.statskeeper.config.ConfigFactoryGUI";
    public static final Logger LOGGER = LogManager.getLogger(StatsKeeper.MOD_NAME);

    public static ProxyBase proxy;
    public static SimpleNetworkWrapper networkWrapper;

    public StatsKeeper() {
        super(MOD_ID, MOD_NAME, VERSION);
        StatsKeeper.proxy = getProxy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerForgeEntries(RegistryList list) {
        if (list.getType() == RegistryType.POTION) {
            ModPotions.init();
            list.addAll(ModPotions.potions);
        }
    }

    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        SKConfig.initialize(event.getModConfigurationDirectory());
        StatsKeeper.initializeCapabilities();
        StatsKeeper.initializeEvents();
        if (Loader.isModLoaded("toughasnails")) {
            networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
            networkWrapper.registerMessage(StatsMessageTAN.MessageHandler.class, StatsMessageTAN.class, 0, Side.CLIENT);
        }
    }

    private static void initializeCapabilities() {
        CapabilityHealth.register();
    }

    private static void initializeEvents() {
        MinecraftForge.EVENT_BUS.register(new BasicEventHandler());
        MinecraftForge.EVENT_BUS.register(new HungerEventHandler());
        MinecraftForge.EVENT_BUS.register(new HealthEventHandler());
        if (Loader.isModLoaded("toughasnails")) MinecraftForge.EVENT_BUS.register(new TANEvent());
    }

    @Mod.EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Mod.EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        SKConfig.syncRegenerativeItems();
    }

    @Override
    public StatsKeeper getInstance() {
        return this;
    }
}
