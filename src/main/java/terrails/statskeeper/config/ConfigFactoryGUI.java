package terrails.statskeeper.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ConfigFactoryGUI implements IModGuiFactory {

    public boolean hasConfigGui() {
        return true;
    }

    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GUIConfig(parentScreen);
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

}