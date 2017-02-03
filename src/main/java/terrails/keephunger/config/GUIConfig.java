package terrails.keephunger.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import terrails.keephunger.MainClass;

public class GUIConfig extends GuiConfig {
    public GUIConfig(GuiScreen parent) {
        super(parent,
                new ConfigElement(ConfigHandler.configFile.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                "keep_hunger", false, false, GuiConfig.getAbridgedConfigPath(ConfigHandler.configFile.toString()));
    }
}