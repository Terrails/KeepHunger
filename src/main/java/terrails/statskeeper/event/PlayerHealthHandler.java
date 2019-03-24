package terrails.statskeeper.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import terrails.statskeeper.api.data.HealthManager;
import terrails.statskeeper.api.event.PlayerCopyCallback;
import terrails.statskeeper.api.event.PlayerJoinCallback;
import terrails.statskeeper.api.event.PlayerUseFinishedCallback;
import terrails.statskeeper.config.SKHealthConfig;
import terrails.statskeeper.health.HealthHelper;

import java.util.Optional;

public class PlayerHealthHandler {

    public static PlayerJoinCallback PLAYER_JOIN = (PlayerEntity player) -> {
        if (!SKHealthConfig.enabled) {
            HealthHelper.removeModifier(player);
            return;
        }

        HealthManager.getInstance(player).ifPresent(HealthManager::update);
    };

    public static PlayerCopyCallback PLAYER_COPY = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        if (!SKHealthConfig.enabled) {
            return;
        }

        HealthManager.getInstance(player).ifPresent(health -> {

            HealthManager.getInstance(oldPlayer).ifPresent(instance -> {
                CompoundTag tag = new CompoundTag();
                instance.serialize(tag);
                health.deserialize(tag);
                health.setHealth(health.getHealth());
            });

            if (SKHealthConfig.starting_health == SKHealthConfig.max_health && SKHealthConfig.min_health <= 0) {
                health.update();
                return;
            }

            if (!isEnd && SKHealthConfig.health_decrease > 0 && health.isHealthRemovable()) {
                int prevHealth = health.getHealth();
                health.addHealth(-SKHealthConfig.health_decrease);
                double removedAmount = health.getHealth() - prevHealth;
                if (SKHealthConfig.health_message && removedAmount > 0) {
                    HealthHelper.playerMessage(player, "health.statskeeper.death_remove", removedAmount);
                }
            }
        });
    };

    public static UseItemCallback ITEM_INTERACT = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator() || world.isClient)
            return ActionResult.PASS;

        if (!SKHealthConfig.enabled) {
            return ActionResult.PASS;
        }

        Optional<HealthManager> optional = HealthManager.getInstance(player);
        if (!optional.isPresent()) {
            return ActionResult.PASS;
        }

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem().getFoodSetting() != null && player.canConsume(stack.getItem().getFoodSetting().isAlwaysEdible())) {
            return ActionResult.PASS;
        }

        if (stack.getUseAction() == UseAction.DRINK) {
            return ActionResult.PASS;
        }

        for (SKHealthConfig.HealthItem healthItem : SKHealthConfig.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            if (optional.get().addHealth(healthItem.getHealthAmount(), !healthItem.doesBypassThreshold())) {
                stack.subtractAmount(1);
                return ActionResult.SUCCESS;
            }

            break;
        }
        return ActionResult.PASS;
    };

    public static PlayerUseFinishedCallback ITEM_USE_FINISHED = (PlayerEntity player, ItemStack stack) -> {
        if (!SKHealthConfig.enabled || player.world.isClient) {
            return;
        }

        for (SKHealthConfig.HealthItem item : SKHealthConfig.health_items) {

            if (item.getItem() != stack.getItem()) {
                continue;
            }

            HealthManager.getInstance(player).ifPresent(health -> health.addHealth(item.getHealthAmount(), !item.doesBypassThreshold()));
            break;
        }
    };
}
