package terrails.statskeeper.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import terrails.statskeeper.api.data.ISaturation;

@Mixin(HungerManager.class)
public class HungerManagerMixin implements ISaturation {

    @Shadow private float foodSaturationLevel;

    @Override
    public void setSaturationLevel(float float_1) {
        this.foodSaturationLevel = float_1;
    }
}
