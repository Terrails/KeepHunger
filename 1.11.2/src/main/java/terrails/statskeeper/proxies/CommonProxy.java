package terrails.statskeeper.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import terrails.statskeeper.Constants;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.event.AppetiteEvent;
import terrails.statskeeper.event.EventHandler;
import terrails.statskeeper.event.TANEvent;
import terrails.statskeeper.event.health.HealthEvent;
import terrails.statskeeper.packet.ThirstMessage;
import terrails.statskeeper.potion.ModPotions;

import static terrails.statskeeper.StatsKeeper.wrapperInstance;

public class CommonProxy {

    @Mod.EventHandler
    @SuppressWarnings("deprecation")
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
        ConfigHandler.init(event.getModConfigurationDirectory());
        ModPotions.init();
        wrapperInstance = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID);
        wrapperInstance.registerMessage(ThirstMessage.MessageHandler.class, ThirstMessage.class, 0, Side.CLIENT);
    }

    @Mod.EventHandler
    @SuppressWarnings("deprecation")
    public void init(FMLInitializationEvent event) {
        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);

        AppetiteEvent appetite = new AppetiteEvent();
        MinecraftForge.EVENT_BUS.register(appetite);

        TANEvent tanEvent = new TANEvent();
        MinecraftForge.EVENT_BUS.register(tanEvent);

        HealthEvent healthEvent = new HealthEvent();
        MinecraftForge.EVENT_BUS.register(healthEvent);
        FMLCommonHandler.instance().bus().register(healthEvent);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
