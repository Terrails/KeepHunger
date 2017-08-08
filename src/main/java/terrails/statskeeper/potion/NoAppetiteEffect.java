package terrails.statskeeper.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import terrails.statskeeper.Constants;
import terrails.terracore.base.PotionBase;

import java.util.ArrayList;
import java.util.List;

public class NoAppetiteEffect extends PotionBase
{
    public NoAppetiteEffect(int id)
    {
        super(Constants.MOD_ID, true, 0, 1, 1);
    }

    public NoAppetiteEffect(int id, String name)
    {
        super(Constants.MOD_ID, true, 0, 1, 1);
        setRegistryName(new ResourceLocation(Constants.MOD_ID, name));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> list = new ArrayList<ItemStack>();
        return list;
    }
}