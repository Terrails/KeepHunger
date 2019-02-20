package terrails.statskeeper.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BucketMilkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrails.statskeeper.api.IEffectCure;
import terrails.statskeeper.api.SKPotions;

@Mixin(BucketMilkItem.class)
public class BucketMilkItemMixin {

    @Inject(method = "onItemFinishedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V"))
    private void onItemFinishedUsing(ItemStack stack, World world, LivingEntity entity, CallbackInfoReturnable<Boolean> returnable) {
        if (entity.hasPotionEffect(SKPotions.NO_APPETITE)) {
            ((IEffectCure) entity).cureStatusEffects(stack);
        }
    }

    @Redirect(method = "onItemFinishedUsing", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    private boolean shouldNotProceed(World theWorld, ItemStack stack, World world, LivingEntity entity) {
        return (entity.hasPotionEffect(SKPotions.NO_APPETITE));
    }
}