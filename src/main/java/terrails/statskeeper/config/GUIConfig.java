package terrails.statskeeper.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Loader;
import terrails.statskeeper.Constants;

import java.util.ArrayList;
import java.util.List;

public class GUIConfig extends GuiConfig {
   /*
    public GUIConfig(GuiScreen parent) {

        super(parent,
                new ConfigElement(ConfigHandler.configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                Constants.MODID, false,
                false, GuiConfig.getAbridgedConfigPath(ConfigHandler.configFile.toString()));
    } */
   public GUIConfig(GuiScreen parentScreen)
   {
       super(parentScreen,
               GUIConfig.getConfigElements(), Constants.MODID,
               false, false, "/" + Constants.MODID + ".cfg");
   }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        List<IConfigElement> GENERAL_SETTINGS = new ConfigElement(ConfigHandler.configFile.getCategory(ConfigHandler.GENERAL_SETTINGS.toLowerCase())).getChildElements();
        List<IConfigElement> MINIMAL_SETTINGS = new ConfigElement(ConfigHandler.configFile.getCategory(ConfigHandler.MINIMAL_SETTINGS.toLowerCase())).getChildElements();
        List<IConfigElement> TAN_SETTINGS = new ConfigElement(ConfigHandler.configFile.getCategory(ConfigHandler.TAN_SETTINGS.toLowerCase())).getChildElements();

        list.add(new DummyConfigElement.DummyCategoryElement(I18n.translateToLocal("config.category.generalSettings.title"), "config.category.arrowSettings", GENERAL_SETTINGS));
        list.add(new DummyConfigElement.DummyCategoryElement(I18n.translateToLocal("config.category.minimalSettings.title"), "config.category.arrowSettings", MINIMAL_SETTINGS));


        if(Loader.isModLoaded("toughasnails")){

            list.add(new DummyConfigElement.DummyCategoryElement(I18n.translateToLocal("config.category.tanSettings.title"), "config.category.arrowSettings", TAN_SETTINGS));
        }
        else if(Loader.isModLoaded("ToughAsNails")){

            list.add(new DummyConfigElement.DummyCategoryElement(I18n.translateToLocal("config.category.tanSettings.title"), "config.category.arrowSettings", TAN_SETTINGS));
        }
    return list;
    }
}