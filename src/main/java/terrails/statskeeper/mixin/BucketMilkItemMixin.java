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
import terrails.statskeeper.effect.IEffectCure;
import terrails.statskeeper.api.effect.SKEffects;

@Mixin(MilkBucketItem.class)
public class BucketMilkItemMixin {

    @Inject(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V"))
    private void onItemFinishedUsing(ItemStack stack, World world, LivingEntity entity, CallbackInfoReturnable<Boolean> returnable) {
        if (entity.hasStatusEffect(SKEffects.NO_APPETITE)) {
            ((IEffectCure) entity).clearPlayerStatusEffects(stack);
        }
    }

    @Redirect(method = "finishUsing", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    private boolean shouldNotProceed(World theWorld, ItemStack stack, World world, LivingEntity entity) {
        return (entity.hasStatusEffect(SKEffects.NO_APPETITE));
    }
}
