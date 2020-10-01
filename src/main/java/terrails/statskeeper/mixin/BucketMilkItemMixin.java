package terrails.statskeeper.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrails.statskeeper.api.effect.IEffectCure;
import terrails.statskeeper.api.effect.SKEffects;

@Mixin(MilkBucketItem.class)
public class BucketMilkItemMixin {

    @Inject(method = "finishUsing", at = @At(value = "RETURN"))
    private void clearStatusEffects(ItemStack stack, World world, LivingEntity entity, CallbackInfoReturnable<Boolean> returnable) {
        if (!world.isClient) {
            if (entity.hasStatusEffect(SKEffects.NO_APPETITE)) {
                ((IEffectCure) entity).clearPlayerStatusEffects(stack);
            } else entity.clearStatusEffects();
        }
    }

    @Redirect(method = "finishUsing", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    private boolean stop(World theWorld, ItemStack stack, World world, LivingEntity entity) {
        return true;
    }
}
