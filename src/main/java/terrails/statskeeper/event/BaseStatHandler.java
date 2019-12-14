package terrails.statskeeper.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import terrails.statskeeper.api.event.PlayerCopyCallback;
import terrails.statskeeper.api.event.PlayerRespawnCallback;
import terrails.statskeeper.api.effect.SKEffects;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.config.SKHungerConfig;
import terrails.statskeeper.mixin.HungerManagerAccessor;

public class BaseStatHandler {

    public static final PlayerCopyCallback PLAYER_COPY = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd) {

            boolean checkGameRule = player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
            if (SKConfig.keep_experience && !checkGameRule) {
                player.addExperience(oldPlayer.experienceLevel);
            }

            if (SKHungerConfig.keep_hunger) {
                int value = Math.max(SKHungerConfig.lowest_hunger, oldPlayer.getHungerManager().getFoodLevel());
                player.getHungerManager().setFoodLevel(value);
            }

            if (SKHungerConfig.keep_saturation) {
                float value = Math.max(SKHungerConfig.lowest_saturation, oldPlayer.getHungerManager().getSaturationLevel());
                HungerManagerAccessor manager = (HungerManagerAccessor) player.getHungerManager();
                manager.setFoodSaturationLevel(value);
            }
        }
    };

    public static final PlayerRespawnCallback PLAYER_RESPAWN = (PlayerEntity player, boolean isEnd) -> {
        if (!isEnd && !player.isCreative() && SKHungerConfig.no_appetite_time > 0) {
            player.addStatusEffect(new StatusEffectInstance(SKEffects.NO_APPETITE, SKHungerConfig.no_appetite_time * 20, 0, false, false, true));
        }
    };

    public static final UseBlockCallback BLOCK_INTERACT = (PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) -> {
        if (player.isSpectator() || !player.hasStatusEffect(SKEffects.NO_APPETITE)) return ActionResult.PASS;

        if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof CakeBlock) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    };

    public static final UseItemCallback ITEM_INTERACT = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator() || !player.hasStatusEffect(SKEffects.NO_APPETITE)) return TypedActionResult.pass(ItemStack.EMPTY);

        ItemStack stack = player.getMainHandStack();
        FoodComponent setting = stack.getItem().getFoodComponent();
        if (setting != null && player.canConsume(setting.isAlwaysEdible())) {
            return TypedActionResult.fail(stack);
        }
        stack = player.getOffHandStack();
        setting = stack.getItem().getFoodComponent();
        if (setting != null && player.canConsume(setting.isAlwaysEdible())) {
            return TypedActionResult.fail(stack);
        }

        return TypedActionResult.pass(ItemStack.EMPTY);
    };
}
