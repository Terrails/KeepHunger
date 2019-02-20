package terrails.statskeeper.event;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.capabilities.IHealth;
import terrails.statskeeper.api.capabilities.SKCapabilities;
import terrails.statskeeper.config.SKConfig;

import java.util.UUID;

public class HealthEventHandler {

    private static final UUID STATS_KEEPER_HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    @SubscribeEvent
    public void playerJoin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        IHealth health = player.getCapability(SKCapabilities.HEALTH_CAPABILITY, null);
        if (health == null)
            return;

        if (SKConfig.Health.enabled) {

            int baseHealth = (int) HealthEventHandler.getAttribute(player).getBaseValue();

            if (!health.isHealthEnabled()) {

                if (SKConfig.Health.min_health_start) {
                    HealthEventHandler.setHealth(player, Operation.MIN);
                } else {
                    HealthEventHandler.setHealth(player, Operation.MAX);
                }
            } else {

                if (!HealthEventHandler.hasModifier(player)) {
                    HealthEventHandler.setAdditionalHealth(player, health.getAdditionalHealth());
                }

                if (SKConfig.Health.on_change_reset) {
                    if (SKConfig.Health.min_health != health.getMinHealth() && SKConfig.Health.min_health_start) {
                        HealthEventHandler.setHealth(player, Operation.MIN);
                    }

                    if (SKConfig.Health.max_health != health.getMaxHealth() && !SKConfig.Health.min_health_start) {
                        HealthEventHandler.setHealth(player, Operation.MAX);
                    }

                    if (health.isMinStart() != SKConfig.Health.min_health_start) {
                        if (SKConfig.Health.min_health_start) {
                            HealthEventHandler.setHealth(player, Operation.MIN);
                        } else {
                            HealthEventHandler.setHealth(player, Operation.MAX);
                        }
                    }
                }

                if (SKConfig.Health.min_health > 0 && health.getAdditionalHealth() < SKConfig.Health.min_health - baseHealth) {
                    HealthEventHandler.setHealth(player, Operation.MIN);
                } else if (health.getAdditionalHealth() > SKConfig.Health.max_health - baseHealth) {
                    HealthEventHandler.setHealth(player, Operation.MAX);
                } else if (health.getAdditionalHealth() < SKConfig.Health.max_health - baseHealth && SKConfig.Health.min_health <= 0) {
                    HealthEventHandler.setHealth(player, Operation.MAX);
                }
            }
        } else {
            HealthEventHandler.removeModifier(player);
            player.setHealth(player.getMaxHealth());
        }
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (SKConfig.Health.enabled) {
            EntityPlayer player = event.getEntityPlayer();
            IHealth oldHealth = event.getOriginal().getCapability(SKCapabilities.HEALTH_CAPABILITY, null);
            IHealth health = player.getCapability(SKCapabilities.HEALTH_CAPABILITY, null);
            if (oldHealth == null || health == null)
                return;

            if (!oldHealth.isHealthEnabled())
                return;

            if (!SKConfig.Health.min_health_start && SKConfig.Health.min_health <= 0) {
                HealthEventHandler.setHealth(player, Operation.MAX);
            }

            if (!event.isWasDeath()) {
                health.setAdditionalHealth(oldHealth.getAdditionalHealth());
                HealthEventHandler.setHealth(player, Operation.SAVE);
                HealthEventHandler.setAdditionalHealth(player, oldHealth.getAdditionalHealth());
            }

            if (SKConfig.Health.health_decrease > 0 && SKConfig.Health.min_health > 0 && event.isWasDeath()) {
                health.setAdditionalHealth(oldHealth.getAdditionalHealth());
                HealthEventHandler.setHealth(player, Operation.REMOVE);

                double removedAmount = (oldHealth.getAdditionalHealth()) - health.getAdditionalHealth();
                if (SKConfig.Health.health_message) {
                    double messageAmount = removedAmount / 2.0;
                    if (messageAmount != 0) {
                        TextComponentTranslation component = new TextComponentTranslation("health.statskeeper.death_remove", (int) messageAmount);
                        if (messageAmount % 1 != 0) component = new TextComponentTranslation("health.statskeeper.death_remove", messageAmount);
                        HealthEventHandler.playerMessage(player, component.getFormattedText());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void itemUseFinished(LivingEntityUseItemEvent.Finish event) {
        if (!SKConfig.Health.enabled || !(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        for (SKConfig.Health.HealthItem healthItem : SKConfig.Health.health_items) {

            if (healthItem.getItem() != event.getItem().getItem())
                continue;

            if (healthItem.getMeta() != event.getItem().getMetadata() && healthItem.getMeta() != OreDictionary.WILDCARD_VALUE)
                continue;

            HealthEventHandler.addHealth((EntityPlayer) event.getEntityLiving(), healthItem.getHealthAmount());
            break;
        }
    }

    @SubscribeEvent
    public void itemUse(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.isSpectator() || event.getWorld().isRemote)
            return;

        ItemStack stack = player.getHeldItem(event.getHand());
        if (stack.getItemUseAction() == EnumAction.EAT || stack.getItemUseAction() == EnumAction.DRINK) {
            return;
        }

        if (!SKConfig.Health.enabled) {
            return;
        }

        for (SKConfig.Health.HealthItem healthItem : SKConfig.Health.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            if (healthItem.getMeta() != stack.getMetadata() && healthItem.getMeta() != OreDictionary.WILDCARD_VALUE)
                continue;

            if (HealthEventHandler.addHealth(player, healthItem.getHealthAmount())) {
                stack.shrink(1);
                return;
            }
            break;
        }
    }

    private static void setHealth(EntityPlayer player, Operation type) {
        IHealth health = player.getCapability(SKCapabilities.HEALTH_CAPABILITY, null);
        int baseHealth = (int) HealthEventHandler.getAttribute(player).getBaseValue();
        if (health == null)
            return;

        health.setHealthEnabled(true);
        health.setMinStart(SKConfig.Health.min_health_start);
        switch (type) {
            case MAX:
                health.setMaxHealth(SKConfig.Health.max_health);
                HealthEventHandler.setAdditionalHealth(player, SKConfig.Health.max_health - baseHealth);
                break;
            case MIN:
                health.setMinHealth(SKConfig.Health.min_health);
                HealthEventHandler.setAdditionalHealth(player, SKConfig.Health.min_health - baseHealth);
                break;
            case REMOVE:
                health.setMaxHealth(SKConfig.Health.max_health);
                health.setMinHealth(SKConfig.Health.min_health);
                int removedHealth = health.getAdditionalHealth() - SKConfig.Health.health_decrease;
                int addedHealth = removedHealth < SKConfig.Health.min_health - baseHealth ? SKConfig.Health.min_health - baseHealth : removedHealth;
                HealthEventHandler.setAdditionalHealth(player, addedHealth);
                break;
            case SAVE:
                health.setMaxHealth(SKConfig.Health.max_health);
                health.setMinHealth(SKConfig.Health.min_health);
                break;
        }
    }

    private static boolean addHealth(EntityPlayer player, int amount) {
        IHealth health = player.getCapability(SKCapabilities.HEALTH_CAPABILITY, null);
        if (health == null)
            return false;

        if (health.getAdditionalHealth() >= SKConfig.Health.max_health - 20) {
            return false;
        }

        if (health.getAdditionalHealth() + amount > SKConfig.Health.max_health - 20) {
            amount = SKConfig.Health.max_health - 20 - health.getAdditionalHealth();
        }

        health.setAdditionalHealth(health.getAdditionalHealth() + amount);
        HealthEventHandler.setAdditionalHealth(player, health.getAdditionalHealth());
        HealthEventHandler.setHealth(player, Operation.SAVE);
        double messageAmount = amount / 2.0;
        TextComponentTranslation component = new TextComponentTranslation("health.statskeeper.item_add", (int) messageAmount);
        if (messageAmount % 1 != 0) component = new TextComponentTranslation("health.statskeeper.item_add", messageAmount);
        HealthEventHandler.playerMessage(player, component.getFormattedText());
        return true;
    }

    private enum Operation {
        MAX, MIN, REMOVE, SAVE
    }

    private static void setAdditionalHealth(EntityPlayer player, int health) {
        IHealth playerHealth = player.getCapability(SKCapabilities.HEALTH_CAPABILITY, null);
        if (playerHealth == null)
            return;
        HealthEventHandler.removeModifier(player);
        playerHealth.setAdditionalHealth(health);
        HealthEventHandler.addModifier(player, health);
        player.setHealth(player.getMaxHealth());
    }

    private static IAttributeInstance getAttribute(EntityPlayer player) {
        return player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
    }

    private static void addModifier(EntityPlayer player, int amount) {
        IAttributeInstance attribute = HealthEventHandler.getAttribute(player);
        attribute.applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, StatsKeeper.MOD_ID, amount, 0));
    }

    private static void removeModifier(EntityPlayer player) {
        AttributeModifier modifier = HealthEventHandler.getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID);
        if (modifier != null) {
            HealthEventHandler.getAttribute(player).removeModifier(modifier);
        }
    }

    private static boolean hasModifier(EntityPlayer player) {
        return HealthEventHandler.getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID) != null;
    }

    private static void playerMessage(EntityPlayer player, String message) {
        if (message.isEmpty()) return;
        player.sendStatusMessage(new TextComponentString(message), true);
    }
}
