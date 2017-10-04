package terrails.statskeeper.potion;

import net.minecraft.item.ItemStack;
import terrails.statskeeper.Constants;
import terrails.terracore.item.PotionBase;

import java.util.ArrayList;
import java.util.List;

public class NoAppetiteEffect extends PotionBase {

    public NoAppetiteEffect(String name) {
        super(name, true, 0, 0, 0, Constants.MOD_ID);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> list = new ArrayList<ItemStack>();
        return list;
    }
}