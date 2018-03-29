package terrails.statskeeper.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import terrails.statskeeper.Constants;
import terrails.terracore.potion.PotionBase;

import java.util.ArrayList;
import java.util.List;

public class NoAppetiteEffect extends PotionBase {

    public NoAppetiteEffect(String name) {
        super(Constants.MOD_ID, true, 0, 0, 0);
        setPotionName(name);
        setRegistryName(new ResourceLocation(Constants.MOD_ID, name));
        setDefaultTextureLocation(Constants.MOD_ID);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}