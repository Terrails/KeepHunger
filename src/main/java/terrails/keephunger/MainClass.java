package terrails.keephunger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import terrails.keephunger.config.ConfigHandler;
import terrails.keephunger.potion.ModPotions;
import terrails.keephunger.proxies.CommonProxy;

@Mod(
        modid = Constants.MODID,
        version = Constants.VERSION,
        name = Constants.NAME,
        acceptedMinecraftVersions = Constants.MCVERSION,
        guiFactory = Constants.GUIFACTORY
)
public class MainClass {
    @SidedProxy(clientSide="terrails.keephunger.proxies.ClientProxy", serverSide="terrails.keephunger.proxies.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
        ModPotions.init();
        ConfigHandler.init(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
}
