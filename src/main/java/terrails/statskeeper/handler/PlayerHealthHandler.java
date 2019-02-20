package terrails.statskeeper.handler;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.data.IPlayerHealth;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.api.event.PlayerCloneCallback;
import terrails.statskeeper.api.event.PlayerJoinCallback;
import terrails.statskeeper.api.event.PlayerUseFinishedCallback;

import java.util.UUID;

public class PlayerHealthHandler {

    private static final UUID STATS_KEEPER_HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    public static PlayerJoinCallback playerJoinEvent = (PlayerEntity player) -> {
        IPlayerHealth health = (IPlayerHealth) player;
        SKConfig.Health config = SKConfig.instance.HEALTH_STATS;

        if (config.enabled) {

            int baseHealth = (int) PlayerHealthHandler.getAttribute(player).getBaseValue();
            if (!health.isSKHealthEnabled()) {

                if (config.min_health_start) {
                    PlayerHealthHandler.setHealth(player, Operation.MIN);
                } else {
                    PlayerHealthHandler.setHealth(player, Operation.MAX);
                }
            } else {

                if (!PlayerHealthHandler.hasModifier(player)) {
                    PlayerHealthHandler.setAdditionalHealth(player, health.getSKAdditionalHealth());
                }

                if (config.on_change_reset) {
                    if (config.min_health != health.getSKMinHealth() && config.min_health_start) {
                        PlayerHealthHandler.setHealth(player, Operation.MIN);
                    }

                    if (config.max_health != health.getSKMaxHealth() && !config.min_health_start) {
                        PlayerHealthHandler.setHealth(player, Operation.MAX);
                    }

                    if (health.isSKMinStart() != config.min_health_start) {
                        if (config.min_health_start) {
                            PlayerHealthHandler.setHealth(player, Operation.MIN);
                        } else {
                            PlayerHealthHandler.setHealth(player, Operation.MAX);
                        }
                    }
                }

                if (config.min_health > 0 && health.getSKAdditionalHealth() < config.min_health - baseHealth) {
                    PlayerHealthHandler.setHealth(player, Operation.MIN);
                } else if (health.getSKAdditionalHealth() > config.max_health - baseHealth) {
                    PlayerHealthHandler.setHealth(player, Operation.MAX);
                } else if (health.getSKAdditionalHealth() < config.max_health - baseHealth && config.min_health <= 0) {
                    PlayerHealthHandler.setHealth(player, Operation.MAX);
                }
            }
        } else {
            PlayerHealthHandler.removeModifier(player);
            player.setHealth(player.getHealthMaximum());
        }
    };

    public static PlayerCloneCallback playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        SKConfig.Health config = SKConfig.instance.HEALTH_STATS;

        if (config.enabled) {

            IPlayerHealth oldHealth = (IPlayerHealth) oldPlayer;
            IPlayerHealth health = (IPlayerHealth) player;
            if (!oldHealth.isSKHealthEnabled())
                return;

            if (!config.min_health_start && config.min_health <= 0) {
                PlayerHealthHandler.setHealth(player, Operation.MAX);
            }

            if (isEnd) {
                health.setSKAdditionalHealth(oldHealth.getSKAdditionalHealth());
                PlayerHealthHandler.setHealth(player, Operation.SAVE);
                PlayerHealthHandler.setAdditionalHealth(player, oldHealth.getSKAdditionalHealth());
            }

            if (config.health_decrease > 0 && config.min_health > 0 && !isEnd) {
                health.setSKAdditionalHealth(oldHealth.getSKAdditionalHealth());
                PlayerHealthHandler.setHealth(player, Operation.REMOVE);

                double removedAmount = (oldHealth.getSKAdditionalHealth()) - health.getSKAdditionalHealth();
                if (config.health_message) {
                    double messageAmount = removedAmount / 2.0;
                    if (messageAmount != 0) {
                        TranslatableTextComponent component = new TranslatableTextComponent("health.statskeeper.death_remove", (int) messageAmount);
                        if (messageAmount % 1 != 0) component = new TranslatableTextComponent("health.statskeeper.death_remove", messageAmount);
                        PlayerHealthHandler.playerMessage(player, component.getFormattedText());
                    }
                }
            }
        }
    };

    public static PlayerUseFinishedCallback itemUseFinishedEvent = (PlayerEntity player, ItemStack stack) -> {
        SKConfig.Health config = SKConfig.instance.HEALTH_STATS;

        if (!config.enabled) {
            return;
        }

        for (SKConfig.Health.HealthItem healthItem : config.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            PlayerHealthHandler.addHealth(player, healthItem.getHealthAmount());
            break;
        }
    };

    public static UseItemCallback itemInteractEvent = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator() || world.isClient)
            return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getUseAction() == UseAction.EAT || stack.getUseAction() == UseAction.DRINK) {
            return ActionResult.PASS;
        }

        SKConfig.Health config = SKConfig.instance.HEALTH_STATS;
        if (!config.enabled) {
            return ActionResult.PASS;
        }

        for (SKConfig.Health.HealthItem healthItem : config.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            if (PlayerHealthHandler.addHealth(player, healthItem.getHealthAmount())) {
                stack.subtractAmount(1);
                return ActionResult.SUCCESS;
            }
            break;
        }
        return ActionResult.PASS;
    };

    private static void setHealth(PlayerEntity player, Operation type) {
        SKConfig.Health config = SKConfig.instance.HEALTH_STATS;
        IPlayerHealth health = (IPlayerHealth) player;
        int baseHealth = (int) PlayerHealthHandler.getAttribute(player).getBaseValue();

        health.setSKHealthEnabled(true);
        health.setSKMinStart(config.min_health_start);
        switch (type) {
            case MAX:
                health.setSKMaxHealth(config.max_health);
                PlayerHealthHandler.setAdditionalHealth(player, config.max_health - baseHealth);
                break;
            case MIN:
                health.setSKMinHealth(config.min_health);
                PlayerHealthHandler.setAdditionalHealth(player, config.min_health - baseHealth);
                break;
            case REMOVE:
                health.setSKMaxHealth(config.max_health);
                health.setSKMinHealth(config.min_health);
                int removedHealth = health.getSKAdditionalHealth() - config.health_decrease;
                int addedHealth = removedHealth < config.min_health - baseHealth ? config.min_health - baseHealth : removedHealth;
                PlayerHealthHandler.setAdditionalHealth(player, addedHealth);
                break;
            case SAVE:
                health.setSKMaxHealth(config.max_health);
                health.setSKMinHealth(config.min_health);
                break;
        }
    }

    private static boolean addHealth(PlayerEntity player, int amount) {
        SKConfig.Health config = SKConfig.instance.HEALTH_STATS;
        IPlayerHealth health = (IPlayerHealth) player;

        if (health.getSKAdditionalHealth() >= config.max_health - 20) {
            return false;
        }

        if (health.getSKAdditionalHealth() + amount > config.max_health - 20) {
            amount = config.max_health - 20 - health.getSKAdditionalHealth();
        }

        health.setSKAdditionalHealth(health.getSKAdditionalHealth() + amount);
        PlayerHealthHandler.setAdditionalHealth(player, health.getSKAdditionalHealth());
        PlayerHealthHandler.setHealth(player, Operation.SAVE);
        double messageAmount = amount / 2.0;
        TranslatableTextComponent component = new TranslatableTextComponent("health.statskeeper.item_add", (int) messageAmount);
        if (messageAmount % 1 != 0) component = new TranslatableTextComponent("health.statskeeper.item_add", messageAmount);
        PlayerHealthHandler.playerMessage(player, component.getFormattedText());
        return true;
    }

    private enum Operation {
        MAX, MIN, REMOVE, SAVE
    }

    private static void setAdditionalHealth(PlayerEntity player, int health) {
        IPlayerHealth playerHealth = (IPlayerHealth) player;
        PlayerHealthHandler.removeModifier(player);
        playerHealth.setSKAdditionalHealth(health);
        PlayerHealthHandler.addModifier(player, health);
        player.setHealth(player.getHealthMaximum());
    }

    private static EntityAttributeInstance getAttribute(PlayerEntity player) {
        return player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
    }

    private static void addModifier(PlayerEntity player, int amount) {
        EntityAttributeInstance attribute = PlayerHealthHandler.getAttribute(player);
        attribute.addModifier(new EntityAttributeModifier(STATS_KEEPER_HEALTH_UUID, StatsKeeper.MOD_ID, amount, EntityAttributeModifier.Operation.ADDITION));
    }

    private static void removeModifier(PlayerEntity player) {
        EntityAttributeModifier modifier = PlayerHealthHandler.getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID);
        if (modifier != null) {
            PlayerHealthHandler.getAttribute(player).removeModifier(modifier);
        }
    }

    private static boolean hasModifier(PlayerEntity player) {
        return PlayerHealthHandler.getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID) != null;
    }

    private static void playerMessage(PlayerEntity player, String message) {
        if (message.isEmpty()) return;
        player.addChatMessage(new StringTextComponent(message), true);
    }
}
