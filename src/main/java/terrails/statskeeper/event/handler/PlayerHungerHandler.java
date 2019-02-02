package terrails.statskeeper.event.handler;

import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import terrails.statskeeper.api.ISaturation;
import terrails.statskeeper.api.SKPotions;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.event.PlayerEvent;

public class PlayerHungerHandler {

    public static PlayerEvent.Clone playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
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

    public static PlayerEvent.Respawn playerRespawnEvent = (PlayerEntity player, boolean isEnd) -> {
        if (!isEnd) {
            SKConfig.Hunger config = SKConfig.instance.HUNGER_STATS;

            if (config.no_appetite_time > 0 && !player.isCreative()) {
                player.addPotionEffect(new StatusEffectInstance(SKPotions.NO_APPETITE, config.no_appetite_time * 20, 0, false, false, config.show_effect_icon));
            }
        }
    };

    public static PlayerInteractionEvent.BlockPositioned blockInteractEvent = (PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction, float hitX, float hitY, float hitZ) -> {
        if (player.isSpectator()) return ActionResult.PASS;

        if (SKConfig.instance.HUNGER_STATS.no_appetite_time > 0 && player.hasPotionEffect(SKPotions.NO_APPETITE) && world.getBlockState(pos).getBlock() instanceof CakeBlock) {
            return ActionResult.FAILURE;
        }
        return ActionResult.PASS;
    };

    public static PlayerInteractionEvent.Item itemInteractEvent = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator()) return ActionResult.PASS;

        if (SKConfig.instance.HUNGER_STATS.no_appetite_time > 0 && player.hasPotionEffect(SKPotions.NO_APPETITE)
                && (player.getMainHandStack().getUseAction() == UseAction.EAT || player.getOffHandStack().getUseAction() == UseAction.EAT)) {
            return ActionResult.FAILURE;
        }
        return ActionResult.PASS;
    };
}
