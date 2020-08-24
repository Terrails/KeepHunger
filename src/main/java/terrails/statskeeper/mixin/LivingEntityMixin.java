package terrails.statskeeper.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.effect.IEffectCure;
import terrails.statskeeper.api.effect.SKEffects;
import terrails.statskeeper.api.event.PlayerUseFinishedCallback;

import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements IEffectCure {

    @Inject(method = "dropXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getCurrentExperience(Lnet/minecraft/entity/player/PlayerEntity;)I"), cancellable = true)
    private void dropExperience(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        //noinspection ConstantConditions
        if (entity instanceof PlayerEntity && !SKConfig.drop_experience) {
            info.cancel();
        }
    }

    @Shadow protected ItemStack activeItemStack;
    @Shadow private @Final Map<StatusEffect, StatusEffectInstance> activeStatusEffects;
    @Shadow protected void onStatusEffectRemoved(StatusEffectInstance statusEffectInstance_1) {}

    @Override
    public void clearPlayerStatusEffects(ItemStack stack) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.world.isClient) {
            return;
        }
        Iterator<StatusEffectInstance> iterator_1 = this.activeStatusEffects.values().iterator();

        while (iterator_1.hasNext()) {
            StatusEffectInstance effect = iterator_1.next();

            if (effect.getEffectType() == SKEffects.NO_APPETITE) {
                continue;
            }

            this.onStatusEffectRemoved(effect);
            iterator_1.remove();
        }
    }

    @Inject(method = "consumeItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;spawnConsumptionEffects(Lnet/minecraft/item/ItemStack;I)V"))
    private void itemUseFinished(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        //noinspection ConstantConditions
        if (entity instanceof PlayerEntity) {
            PlayerUseFinishedCallback.EVENT.invoker().onItemUseFinished((PlayerEntity) entity, this.activeItemStack);
        }
    }
}
