package terrails.statskeeper.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.*;
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
            return TypedActionResult.pass(ItemStack.EMPTY);

        if (!SKHealthConfig.enabled) {
            return TypedActionResult.pass(ItemStack.EMPTY);
        }

        Optional<HealthManager> optional = HealthManager.getInstance(player);
        if (!optional.isPresent()) {
            return TypedActionResult.pass(ItemStack.EMPTY);
        }

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem().getFoodComponent() != null && player.canConsume(stack.getItem().getFoodComponent().isAlwaysEdible())) {
            return TypedActionResult.pass(ItemStack.EMPTY);
        }

        if (stack.getUseAction() == UseAction.DRINK) {
            return TypedActionResult.pass(ItemStack.EMPTY);
        }

        for (SKHealthConfig.HealthItem healthItem : SKHealthConfig.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            if (optional.get().addHealth(healthItem.getHealthAmount(), !healthItem.doesBypassThreshold())) {
                stack.decrement(1);
                return TypedActionResult.success(stack);
            }

            break;
        }
        return TypedActionResult.pass(ItemStack.EMPTY);
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
