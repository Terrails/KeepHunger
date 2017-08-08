package terrails.statskeeper.potion;

import net.minecraft.item.ItemStack;
import terrails.statskeeper.Constants;
import terrails.terracore.base.PotionBase;

import java.util.ArrayList;
import java.util.List;

public class NoAppetiteEffect extends PotionBase
{
    public NoAppetiteEffect(int id)
    {
        super(Constants.MOD_ID,true, 0, 1, 1);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> list = new ArrayList<ItemStack>();
        return list;
    }
}