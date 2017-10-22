package terrails.statskeeper.potion;

import terrails.statskeeper.Constants;
import terrails.terracore.item.PotionBase;

import java.util.ArrayList;
import java.util.List;

public class NoAppetiteEffect extends PotionBase {

    public NoAppetiteEffect(String name) {
        super(name, true, 0, 0, 0);
        setDefaultTextureLocation(Constants.MOD_ID);
    }

    public List getCurativeItems()
    {
        List list = new ArrayList();
        return list;
    }
}