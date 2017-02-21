package terrails.keephunger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import terrails.keephunger.config.ConfigHandler;
import terrails.keephunger.potion.ModPotions;
import terrails.keephunger.proxies.CommonProxy;
import toughasnails.core.ToughAsNails;

import java.lang.reflect.Field;

@Mod(
        modid = Constants.MODID,
        version = Constants.VERSION,
        name = Constants.NAME,
        acceptedMinecraftVersions = Constants.MCVERSION,
        guiFactory = Constants.GUIFACTORY
)
public class MainClass {
    @SidedProxy(clientSide = Constants.CLIENT_PROXY, serverSide = Constants.SERVER_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        ModPotions.init();
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        if(Loader.isModLoaded("toughasnails")){
            System.out.println("Keep Hunger TAN addon activated!");
        }
        else if(Loader.isModLoaded("ToughAsNails")){
            System.out.println("Keep Hunger TAN addon activated!");

        }
        else{
            System.out.println("Keep Hunger TAN addon not activated!");
        }
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
