package terrails.statskeeper.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Loader;
import terrails.statskeeper.StatsKeeper;

import java.util.ArrayList;
import java.util.List;

public class GUIConfig extends GuiConfig {

   public GUIConfig(GuiScreen parentScreen) {
       super(parentScreen,
               GUIConfig.getConfigElements(), StatsKeeper.MOD_ID,
               false, false, "/" + StatsKeeper.MOD_ID + ".cfg");
   }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> BASIC = new ConfigElement(SKConfig.configuration.getCategory(SKConfig.Categories.BASIC)).getChildElements();
        List<IConfigElement> HUNGER = new ConfigElement(SKConfig.configuration.getCategory(SKConfig.Categories.HUNGER)).getChildElements();
        List<IConfigElement> MOD_COMP = new ConfigElement(SKConfig.configuration.getCategory(SKConfig.Categories.MOD_COMP)).getChildElements();

        List<IConfigElement> list = new ArrayList<>(BASIC);
        list.add(new SKDummyCategoryElement("Hunger", HUNGER));
        list.add(new SKDummyCategoryElement("Health", Health.class));

        if (Loader.isModLoaded("toughasnails")) {
            list.add(new SKDummyCategoryElement("Mod Compatibility", ModCompatibility.class));
        }
        return list;
    }

    public static class Health extends GuiConfigEntries.CategoryEntry {
        public Health(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(owningScreen, new ConfigElement(SKConfig.configuration.getCategory(SKConfig.Categories.HEALTH)).getChildElements(), owningScreen.modID,
                    true, false, "/" + StatsKeeper.MOD_ID + ".cfg");
        }
    }
    public static class ModCompatibility extends GuiConfigEntries.CategoryEntry {
        public ModCompatibility (GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return new GuiConfig(owningScreen, ModCompatibility.getConfigElements(), owningScreen.modID, 
                    false, false, "/" + StatsKeeper.MOD_ID + ".cfg");
        }

        private static List<IConfigElement> getConfigElements() {
            List<IConfigElement> TAN = new ConfigElement(SKConfig.configuration.getCategory(SKConfig.Categories.TOUGH_AS_NAILS)).getChildElements();
            List<IConfigElement> list = new ArrayList<>();

            if (Loader.isModLoaded("toughasnails")) {
                list.add(new SKDummyCategoryElement("ToughAsNails", TAN));
            }
            return list;
        }
    }
    private static class SKDummyCategoryElement extends DummyCategoryElement {

        private SKDummyCategoryElement(String name, List<IConfigElement> childElements) {
            super(name, "", childElements);
        }

        private SKDummyCategoryElement(String name, Class<? extends GuiConfigEntries.IConfigEntry> customListEntryClass) {
            super(name, "", customListEntryClass);
        }

        @Override
        public String getLanguageKey() {
            return "";
        }

        @Override
        public String getComment() {
            return "";
        }
    }
}