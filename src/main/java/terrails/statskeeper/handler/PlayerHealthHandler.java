package terrails.statskeeper.handler;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.data.IAlwaysConsumable;
import terrails.statskeeper.api.data.health.IHealth;
import terrails.statskeeper.api.data.health.IHealthManager;
import terrails.statskeeper.api.event.PlayerCloneCallback;
import terrails.statskeeper.api.event.PlayerJoinCallback;
import terrails.statskeeper.api.event.PlayerUseFinishedCallback;
import terrails.statskeeper.config.SKHealthConfig;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerHealthHandler {

    private static final UUID STATS_KEEPER_HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    public static PlayerJoinCallback playerJoinEvent = (PlayerEntity player) -> {
        IHealth health = IHealthManager.getInstance(player);

        if (SKHealthConfig.enabled) {

            if (!health.isHealthEnabled() || hasConfigChanged(player)) {
                clearHealthData(player);
                setHealth(player, Operation.STARTING);
            } else {

                updateThreshold(player);
                if (!hasModifier(player)) {
                    setAdditionalHealth(player, health.getAdditionalHealth());
                }

                if (health.getCurrentThreshold() > 0 && getCurrentHealth(player) < health.getCurrentThreshold()) {
                    setHealth(player, Operation.THRESHOLD);
                } else if (getCurrentHealth(player) < SKHealthConfig.min_health) {
                    setHealth(player, Operation.MIN);
                } else if (getCurrentHealth(player) > SKHealthConfig.max_health) {
                    setHealth(player, Operation.MAX);
                } else if (getCurrentHealth(player) < SKHealthConfig.max_health && SKHealthConfig.min_health <= 0) {
                    setHealth(player, Operation.MAX);
                }
            }

        } else if (hasModifier(player)) {
            removeModifier(player);
            player.setHealth(player.getHealthMaximum());
        }
    };

    public static PlayerCloneCallback playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {

        if (SKHealthConfig.enabled) {

            IHealth oldHealth = IHealthManager.getInstance(oldPlayer);
            IHealth health = IHealthManager.getInstance(player);
            if (!oldHealth.isHealthEnabled())
                return;

            if (SKHealthConfig.starting_health == SKHealthConfig.max_health && SKHealthConfig.min_health <= 0) {
                setHealth(player, Operation.MAX);
                return;
            }

            setHealth(player, Operation.SAVE);
            health.setAdditionalHealth(oldHealth.getAdditionalHealth());
            health.setCurrentThreshold(oldHealth.getCurrentThreshold());
            updateThreshold(player);

            if (!isEnd && SKHealthConfig.health_decrease > 0 && SKHealthConfig.min_health > 0 && getCurrentHealth(player) > Math.abs(health.getCurrentThreshold())) {
                setHealth(player, Operation.REMOVE);
                double removedAmount = (oldHealth.getAdditionalHealth()) - health.getAdditionalHealth();
                if (SKHealthConfig.health_message && removedAmount > 0) {
                    playerMessage(player, "health.statskeeper.death_remove", removedAmount);
                }
            } else setAdditionalHealth(player, oldHealth.getAdditionalHealth());
        }
    };

    public static PlayerUseFinishedCallback itemUseFinishedEvent = (PlayerEntity player, ItemStack stack) -> {
        if (!SKHealthConfig.enabled || player.world.isClient) {
            return;
        }

        for (SKHealthConfig.HealthItem healthItem : SKHealthConfig.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            addHealth(player, healthItem.getHealthAmount());
            break;
        }
    };

    public static UseItemCallback itemInteractEvent = (PlayerEntity player, World world, Hand hand) -> {
        if (player.isSpectator() || world.isClient)
            return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof FoodItem && player.canConsume(((IAlwaysConsumable) stack.getItem()).isAlwaysConsumable())) {
            return ActionResult.PASS;
        }

        if (stack.getUseAction() == UseAction.DRINK) {
            return ActionResult.PASS;
        }

        if (!SKHealthConfig.enabled) {
            return ActionResult.PASS;
        }

        for (SKHealthConfig.HealthItem healthItem : SKHealthConfig.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            if (addHealth(player, healthItem.getHealthAmount())) {
                stack.subtractAmount(1);
                return ActionResult.SUCCESS;
            }
            break;
        }
        return ActionResult.PASS;
    };

    private static void setHealth(PlayerEntity player, Operation type) {
        IHealth health = IHealthManager.getInstance(player);
        int baseHealth = (int) getAttribute(player).getBaseValue();

        health.setHealthEnabled(true);
        switch (type) {
            case STARTING:
                health.setStartingHealth(SKHealthConfig.starting_health);
                setAdditionalHealth(player, SKHealthConfig.starting_health - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case MAX:
                health.setMaxHealth(SKHealthConfig.max_health);
                setAdditionalHealth(player, SKHealthConfig.max_health - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case MIN:
                health.setMinHealth(SKHealthConfig.min_health);
                setAdditionalHealth(player, SKHealthConfig.min_health - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case THRESHOLD:
                setAdditionalHealth(player, health.getCurrentThreshold() - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case REMOVE:
                health.setMaxHealth(SKHealthConfig.max_health);
                health.setMinHealth(SKHealthConfig.min_health);
                int min_health = Math.max(SKHealthConfig.min_health, Math.abs(health.getCurrentThreshold()));
                int removedHealth = getCurrentHealth(player) - SKHealthConfig.health_decrease;
                int addedHealth = Math.max(removedHealth, min_health) - baseHealth;
                setAdditionalHealth(player, addedHealth);
                setHealth(player, Operation.SAVE);
                break;
            case SAVE:
                health.setMaxHealth(SKHealthConfig.max_health);
                health.setMinHealth(SKHealthConfig.min_health);
                health.setStartingHealth(SKHealthConfig.starting_health);
                break;
        }
    }

    private static boolean addHealth(PlayerEntity player, int amount) {
        IHealth health = IHealthManager.getInstance(player);
        if (amount > 0) {

            if (getCurrentHealth(player) >= SKHealthConfig.max_health) {
                return false;
            }

            if (getCurrentHealth(player) + amount > SKHealthConfig.max_health) {
                amount = SKHealthConfig.max_health - getCurrentHealth(player);
            }
        } else if (amount < 0) {

            if (getCurrentHealth(player) <= SKHealthConfig.min_health) {
                return false;
            }

            if (getCurrentHealth(player) + amount < SKHealthConfig.min_health) {
                amount = SKHealthConfig.min_health - getCurrentHealth(player);
            }
        }

        health.setAdditionalHealth(health.getAdditionalHealth() + amount);
        setAdditionalHealth(player, health.getAdditionalHealth());
        setHealth(player, Operation.SAVE);
        if (!updateThreshold(player)) {
            String key = amount > 0 ? "health.statskeeper.item_add" : "health.statskeeper.item_lose";
            playerMessage(player, key, Math.abs(amount));
        }
        return true;
    }

    private static void setAdditionalHealth(PlayerEntity player, int value) {
        IHealth health = IHealthManager.getInstance(player);
        removeModifier(player);
        health.setAdditionalHealth(value);
        addModifier(player, value);
        player.setHealth(player.getHealthMaximum());
    }

    private enum Operation {
        STARTING, MAX, MIN, THRESHOLD, REMOVE, SAVE
    }

    private static boolean hasConfigChanged(PlayerEntity player) {
        IHealth health = IHealthManager.getInstance(player);

        for (String string : SKHealthConfig.on_change_reset) {
            string = string.toUpperCase();

            if (string.equals("MIN_HEALTH") && SKHealthConfig.min_health != health.getMinHealth()) {
                return true;
            }

            if (string.equals("MAX_HEALTH") && SKHealthConfig.max_health != health.getMaxHealth()) {
                return true;
            }

            if (string.equals("STARTING_HEALTH") && SKHealthConfig.starting_health != health.getStartingHealth()) {
                return true;
            }
        }
        return false;
    }

    private static int getCurrentHealth(PlayerEntity player) {
        IHealth health = IHealthManager.getInstance(player);
        int baseHealth = (int) getAttribute(player).getBaseValue();
        return health.getAdditionalHealth() + baseHealth;
    }

    private static boolean updateThreshold(PlayerEntity player) {
        IHealth health = IHealthManager.getInstance(player);

        if (SKHealthConfig.health_thresholds.length == 0) {
            health.setCurrentThreshold(0);
            return false;
        }

        List<Integer> thresholdList = Arrays.stream(SKHealthConfig.health_thresholds).boxed().collect(Collectors.toList());

        int oldThreshold = health.getCurrentThreshold();
        if ((health.getCurrentThreshold() != 0 && !thresholdList.contains(health.getCurrentThreshold())) || getCurrentHealth(player) < health.getCurrentThreshold()) {
            Stream<Integer> stream = thresholdList.stream().filter(i -> Math.abs(i) <= getCurrentHealth(player));
            int value = stream.reduce((first, second) -> second).orElse(thresholdList.get(0));
            health.setCurrentThreshold(value);
        }

        for (int i : SKHealthConfig.health_thresholds) {

            if (health.getCurrentThreshold() == 0 && i < 0) {
                health.setCurrentThreshold(i);
                break;
            }

            if (Math.abs(health.getCurrentThreshold()) < Math.abs(i) && getCurrentHealth(player) >= Math.abs(i)) {
                health.setCurrentThreshold(i);
            }
        }

        if ((oldThreshold != health.getCurrentThreshold()) && (oldThreshold != 0 || thresholdList.get(0) > 0)) {
            playerMessage(player, "health.statskeeper.threshold", Math.abs(health.getCurrentThreshold()));
            return true;
        }
        return false;
    }

    private static void clearHealthData(PlayerEntity player) {
        IHealth health = IHealthManager.getInstance(player);
        health.setAdditionalHealth(0);
        health.setStartingHealth(0);
        health.setCurrentThreshold(0);
        health.setMinHealth(0);
        health.setMaxHealth(0);
        health.setHealthEnabled(false);
    }

    private static EntityAttributeInstance getAttribute(PlayerEntity player) {
        return player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
    }

    private static void addModifier(PlayerEntity player, int amount) {
        EntityAttributeInstance attribute = getAttribute(player);
        attribute.addModifier(new EntityAttributeModifier(STATS_KEEPER_HEALTH_UUID, StatsKeeper.MOD_ID, amount, EntityAttributeModifier.Operation.ADDITION));
    }

    private static void removeModifier(PlayerEntity player) {
        EntityAttributeModifier modifier = getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID);
        if (modifier != null) {
            getAttribute(player).removeModifier(modifier);
        }
    }

    private static boolean hasModifier(PlayerEntity player) {
        return getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID) != null;
    }

    private static void playerMessage(PlayerEntity player, String key, double health) {
        if (health == 0) return;
        double messageAmount = health / 2.0;
        TranslatableTextComponent component = messageAmount % 1 != 0 ? new TranslatableTextComponent(key, messageAmount) : new TranslatableTextComponent(key, (int) messageAmount);
        player.addChatMessage(component, true);
    }
}
