package terrails.statskeeper.api.effect;

import net.minecraft.item.ItemStack;

public interface IEffectCure {

    void clearPlayerStatusEffects(ItemStack stack);
}
