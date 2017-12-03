package terrails.statskeeper;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
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
import terrails.statskeeper.proxies.IProxy;

@Mod(modid = Constants.MOD_ID,
        name = Constants.MOD_NAME,
        version = Constants.VERSION,
        acceptedMinecraftVersions = Constants.MC_VERSION,
        guiFactory = Constants.GUI_FACTORY,
        dependencies = "required-after:terracore@[" + Constants.TERRACORE_VERSION + ",);")
public class StatsKeeper {
    @SidedProxy(clientSide = Constants.CLIENT_PROXY, serverSide = Constants.SERVER_PROXY)
    public static IProxy proxy;
    public static SimpleNetworkWrapper networkWrapper;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        ConfigHandler.init(event.getModConfigurationDirectory());

        ModPotions.init();
        CapabilityHealth.register();
        CapabilityTAN.register();

        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID);
        networkWrapper.registerMessage(ThirstMessage.MessageHandler.class, ThirstMessage.class, 0, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
