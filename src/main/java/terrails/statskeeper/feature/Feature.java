package terrails.statskeeper.feature;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public abstract class Feature {

    protected final List<Runnable> runnables = new ArrayList<>();

    public abstract String name();

    public abstract void setupConfig(final ForgeConfigSpec.Builder builder);

    /**
     * @return if a certain mod is loaded or something
     */
    public boolean canLoad() {
        return true;
    }

    public void registerEventBus() {
        if (canLoad()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public void configLoad() {
        runnables.forEach(Runnable::run);
    }
}
