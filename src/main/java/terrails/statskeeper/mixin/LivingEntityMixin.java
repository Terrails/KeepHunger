package terrails.statskeeper.mixin;

import net.fabricmc.fabric.util.HandlerArray;
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
import terrails.statskeeper.api.IEffectCure;
import terrails.statskeeper.api.SKPotions;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.event.InteractEvent;

import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements IEffectCure {

    @ModifyVariable(method = "updatePostDeath()V", ordinal = 0, at = @At(value = "STORE", ordinal = 0), require = 1)
    private int dropExperience(int amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        //noinspection ConstantConditions
        if (!SKConfig.instance.drop_experience && entity instanceof PlayerEntity) {
            return 0;
        }
        return amount;
    }

    @Shadow protected ItemStack activeItemStack;
    @Shadow private @Final Map<StatusEffect, StatusEffectInstance> activePotionEffects;
    @Shadow protected void method_6129(StatusEffectInstance statusEffectInstance_1) {}

    @Override
    public boolean cureStatusEffects(ItemStack stack) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.world.isClient) {
            return false;
        } else {
            Iterator<StatusEffectInstance> iterator_1 = this.activePotionEffects.values().iterator();

            boolean boolean_1;
            for(boolean_1 = false; iterator_1.hasNext(); boolean_1 = true) {
                StatusEffectInstance effect = iterator_1.next();

                if (effect.getEffectType()  == SKPotions.NO_APPETITE) {
                    continue;
                }

                this.method_6129(effect);
                iterator_1.remove();
            }

            return boolean_1;
        }
    }

    @Inject(method = "method_6040()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;method_6098(Lnet/minecraft/item/ItemStack;I)V"))
    private void itemUseFinished(CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        //noinspection ConstantConditions
        if (entity instanceof PlayerEntity) {
            HandlerArray<InteractEvent.UseFinished> handler = InteractEvent.PLAYER_USE_FINISHED;

            for (InteractEvent.UseFinished event : handler.getBackingArray()) {
                event.onItemUseFinished((PlayerEntity) entity, this.activeItemStack);
            }
        }
    }
}
