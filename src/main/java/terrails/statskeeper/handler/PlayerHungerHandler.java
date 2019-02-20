package terrails.statskeeper.handler;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import terrails.statskeeper.api.data.ISaturation;
import terrails.statskeeper.api.potion.SKPotions;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.api.event.PlayerCloneCallback;
import terrails.statskeeper.api.event.PlayerRespawnCallback;

public class PlayerHungerHandler {

    public static PlayerCloneCallback playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd) {
            SKConfig.Hunger config = SKConfig.instance.HUNGER_STATS;

            if (config.keep_hunger) {
                int hungerValue = oldPlayer.getHungerManager().getFoodLevel();
                if (config.lowest_hunger > hungerValue) {
                    hungerValue = config.lowest_hunger;
                }
                player.getHungerManager().setFoodLevel(hungerValue);
            }

            if (config.keep_saturation) {
                HungerManager foodStats = player.getHungerManager();
                float saturationLevel = oldPlayer.getHungerManager().getSaturationLevel();
                if (config.lowest_saturation > saturationLevel) {
                    saturationLevel = config.lowest_saturation;
                }

                ISaturation manager = (ISaturation) foodStats;
                manager.setSaturationLevel(saturationLevel);
            }
        }
    };

    public static PlayerRespawnCallback playerRespawnEvent = (PlayerEntity player, boolean isEnd) -> {
        if (!isEnd) {
            SKConfig.Hunger config = SKConfig.instance.HUNGER_STATS;

            if (config.no_appetite_time > 0 && !player.isCreative()) {
                player.addPotionEffect(new StatusEffectInstance(SKPotions.NO_APPETITE, config.no_appetite_time * 20, 0, false, false, config.show_effect_icon));
            }
        }
    };

    public static UseBlockCallback blockInteractEvent = (PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) -> {
        if (player.isSpectator()) return ActionResult.PASS;

        if (SKConfig.instance.HUNGER_STATS.no_appetite_time > 0 && player.hasPotionEffect(SKPotions.NO_APPETITE)
                && world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof CakeBlock) {
            return ActionResult.FAILURE;
        }
        return ActionResult.PASS;
    };

    public static UseItemCallback itemInteractEvent = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator()) return ActionResult.PASS;

        if (SKConfig.instance.HUNGER_STATS.no_appetite_time > 0 && player.hasPotionEffect(SKPotions.NO_APPETITE)
                && (player.getMainHandStack().getUseAction() == UseAction.EAT || player.getOffHandStack().getUseAction() == UseAction.EAT)) {
            return ActionResult.FAILURE;
        }
        return ActionResult.PASS;
    };
}
