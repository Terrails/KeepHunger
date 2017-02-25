package terrails.statskeeper.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.event.AppetiteEvent;
import terrails.statskeeper.event.EventHandler;
import terrails.statskeeper.event.TANEvent;

public class CommonProxy {


    public void preInit(FMLPreInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
    }

    public void init(FMLInitializationEvent e) {
        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);

        AppetiteEvent appetite = new AppetiteEvent();
        MinecraftForge.EVENT_BUS.register(appetite);

        TANEvent tanEvent = new TANEvent();
        MinecraftForge.EVENT_BUS.register(tanEvent);

    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}