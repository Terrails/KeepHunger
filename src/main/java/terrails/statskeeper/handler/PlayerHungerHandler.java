package terrails.statskeeper.handler;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import terrails.statskeeper.api.data.ISaturation;
import terrails.statskeeper.api.potion.SKPotions;
import terrails.statskeeper.api.event.PlayerCloneCallback;
import terrails.statskeeper.api.event.PlayerRespawnCallback;
import terrails.statskeeper.config.SKHungerConfig;

public class PlayerHungerHandler {

    public static PlayerCloneCallback playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd) {

            if (SKHungerConfig.keep_hunger) {
                int value = Math.max(SKHungerConfig.lowest_hunger, oldPlayer.getHungerManager().getFoodLevel());
                player.getHungerManager().setFoodLevel(value);
            }

            if (SKHungerConfig.keep_saturation) {
                float value = Math.max(SKHungerConfig.lowest_saturation, oldPlayer.getHungerManager().getSaturationLevel());
                ISaturation manager = (ISaturation) player.getHungerManager();
                manager.setSaturationLevel(value);
            }
        }
    };

    public static PlayerRespawnCallback playerRespawnEvent = (PlayerEntity player, boolean isEnd) -> {
        if (!isEnd) {

            if (SKHungerConfig.no_appetite_time > 0 && !player.isCreative()) {
                player.addPotionEffect(new StatusEffectInstance(SKPotions.NO_APPETITE, SKHungerConfig.no_appetite_time * 20, 0, false, false, SKHungerConfig.show_effect_icon));
            }
        }
    };

    public static UseBlockCallback blockInteractEvent = (PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) -> {
        if (player.isSpectator()) return ActionResult.PASS;

        if (SKHungerConfig.no_appetite_time > 0 && player.hasPotionEffect(SKPotions.NO_APPETITE)
                && world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof CakeBlock) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    };

    public static UseItemCallback itemInteractEvent = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator()) return ActionResult.PASS;

        if (SKHungerConfig.no_appetite_time > 0 && player.hasPotionEffect(SKPotions.NO_APPETITE)
                && (player.getMainHandStack().getUseAction() == UseAction.EAT || player.getOffHandStack().getUseAction() == UseAction.EAT)) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    };
}
