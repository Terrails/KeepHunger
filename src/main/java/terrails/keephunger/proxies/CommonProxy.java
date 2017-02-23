package terrails.keephunger.proxies;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import terrails.keephunger.config.ConfigHandler;
import terrails.keephunger.event.AppetiteEvent;
import terrails.keephunger.event.EventHandler;
import terrails.keephunger.event.TANEvent;

public class CommonProxy {


    public void preInit(FMLPreInitializationEvent e) {
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
    }

    public void init(FMLInitializationEvent e) {
        EventHandler handler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);

        AppetiteEvent appetite = new AppetiteEvent();
        MinecraftForge.EVENT_BUS.register(appetite);
        FMLCommonHandler.instance().bus().register(appetite);

        TANEvent tanEvent = new TANEvent();
        MinecraftForge.EVENT_BUS.register(tanEvent);
        FMLCommonHandler.instance().bus().register(tanEvent);

    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}