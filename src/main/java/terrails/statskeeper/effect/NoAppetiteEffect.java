package terrails.statskeeper.effect;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import terrails.statskeeper.StatsKeeper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NoAppetiteEffect extends Effect {

    public NoAppetiteEffect() {
        super(EffectType.HARMFUL, new Color(72, 120, 68).getRGB());
        this.setRegistryName(new ResourceLocation(StatsKeeper.MOD_ID, "no_appetite"));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}