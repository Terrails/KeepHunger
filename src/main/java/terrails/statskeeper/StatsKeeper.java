package terrails.statskeeper;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import terrails.statskeeper.data.capabilities.health.CapabilityHealth;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.data.capabilities.tan.CapabilityTAN;
import terrails.statskeeper.packet.ThirstMessage;
import terrails.statskeeper.potion.ModPotions;
import terrails.terracore.base.MainModClass;
import terrails.terracore.base.proxies.ProxyBase;
import terrails.terracore.base.registry.RegistryList;

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

    public static ProxyBase proxy;
    public static SimpleNetworkWrapper networkWrapper;

    public StatsKeeper() {
        super(MOD_ID, MOD_NAME, VERSION);
        StatsKeeper.proxy = getProxy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerForgeEntries(RegistryList list) {
        switch (list.getType()) {
            case POTION:
                list.addAll(ModPotions.potions);
                break;
        }
    }

    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ConfigHandler.init(event.getModConfigurationDirectory());

        ModPotions.init();
        CapabilityHealth.register();
        CapabilityTAN.register();

        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
        networkWrapper.registerMessage(ThirstMessage.MessageHandler.class, ThirstMessage.class, 0, Side.CLIENT);
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
    }

    @Override
    public StatsKeeper getInstance() {
        return this;
    }
}
