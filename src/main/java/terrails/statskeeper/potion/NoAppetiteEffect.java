package terrails.statskeeper.potion;

import java.util.ArrayList;
import java.util.List;

import terrails.terracore.base.PotionBase;

public class NoAppetiteEffect extends PotionBase {

    public NoAppetiteEffect(int id) {
        super("stats_keeper", true, 0, 1, 1);
    }

    public List getCurativeItems()
    {
        List list = new ArrayList();
        return list;
    }
}