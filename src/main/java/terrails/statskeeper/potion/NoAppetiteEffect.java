package terrails.statskeeper.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import terrails.statskeeper.Constants;
import terrails.statskeeper.StatsKeeper;
import terrails.terracore.potion.PotionBase;

import java.util.ArrayList;
import java.util.List;

public class NoAppetiteEffect extends PotionBase {

    public NoAppetiteEffect(String name) {
        super(true, 0, 0, 0);
        setPotionName(StatsKeeper.MOD_ID + "." + name);
        setRegistryName(new ResourceLocation(StatsKeeper.MOD_ID, name));
        setDefaultTextureLocation(StatsKeeper.MOD_ID);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}