package terrails.statskeeper.mixin;

import net.minecraft.item.FoodItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import terrails.statskeeper.api.data.IAlwaysConsumable;

@Mixin(FoodItem.class)
public class FoodItemMixin implements IAlwaysConsumable {

    @Shadow private boolean alwaysConsumable;

    @Override
    public boolean isAlwaysConsumable() {
        return this.alwaysConsumable;
    }
}
