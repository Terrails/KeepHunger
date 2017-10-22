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

    @SuppressWarnings( "deprecation" )
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return GUIConfig.class;
    }

    @SuppressWarnings( "deprecation" )
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

}