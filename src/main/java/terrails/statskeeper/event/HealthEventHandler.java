package terrails.statskeeper.event;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
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
import terrails.statskeeper.config.configs.SKHealthConfig;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HealthEventHandler {

    private static final UUID STATS_KEEPER_HEALTH_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

    @SubscribeEvent
    public void playerJoin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        IHealth health = SKCapabilities.getCapability(player);

        if (SKHealthConfig.enabled) {

            if (!health.isHealthEnabled() || this.hasConfigChanged(player)) {
                this.clearHealthData(player);
                this.setHealth(player, Operation.STARTING);
            } else {

                this.updateThreshold(player);
                if (!hasModifier(player)) {
                    this.setAdditionalHealth(player, health.getAdditionalHealth());
                }

                if (health.getCurrentThreshold() > 0 && this.getCurrentHealth(player) < health.getCurrentThreshold()) {
                    this.setHealth(player, Operation.THRESHOLD);
                } else if (this.getCurrentHealth(player) < SKHealthConfig.min_health) {
                    this.setHealth(player, Operation.MIN);
                } else if (this.getCurrentHealth(player) > SKHealthConfig.max_health) {
                    this.setHealth(player, Operation.MAX);
                } else if (this.getCurrentHealth(player) < SKHealthConfig.max_health && SKHealthConfig.min_health <= 0) {
                    this.setHealth(player, Operation.MAX);
                }
            }

        } else if (this.hasModifier(player)) {
            this.removeModifier(player);
            player.setHealth(player.getMaxHealth());
        }
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (SKHealthConfig.enabled) {
            EntityPlayer player = event.getEntityPlayer();
            IHealth oldHealth = SKCapabilities.getCapability(event.getOriginal());
            IHealth health = SKCapabilities.getCapability(player);

            if (!oldHealth.isHealthEnabled())
                return;

            if (SKHealthConfig.starting_health == SKHealthConfig.max_health && SKHealthConfig.min_health <= 0) {
                this.setHealth(player, Operation.MAX);
                return;
            }

            this.setHealth(player, Operation.SAVE);
            health.setAdditionalHealth(oldHealth.getAdditionalHealth());
            health.setCurrentThreshold(oldHealth.getCurrentThreshold());
            this.updateThreshold(player);

            if (event.isWasDeath() && SKHealthConfig.health_decrease > 0 && SKHealthConfig.min_health > 0 && this.getCurrentHealth(player) > Math.abs(health.getCurrentThreshold())) {
                this.setHealth(player, Operation.REMOVE);
                double removedAmount = (oldHealth.getAdditionalHealth()) - health.getAdditionalHealth();
                if (SKHealthConfig.health_message && removedAmount > 0) {
                    this.playerMessage(player, "health.statskeeper.death_remove", removedAmount);
                }
            } else this.setAdditionalHealth(player, oldHealth.getAdditionalHealth());
        }
    }

    @SubscribeEvent
    public void itemUseFinished(LivingEntityUseItemEvent.Finish event) {
        if (!SKHealthConfig.enabled || !(event.getEntity() instanceof EntityPlayerMP)) {
            return;
        }

        for (SKHealthConfig.HealthItem healthItem : SKHealthConfig.health_items) {

            if (healthItem.getItem() != event.getItem().getItem())
                continue;

            if (healthItem.getMeta() != event.getItem().getMetadata() && healthItem.getMeta() != OreDictionary.WILDCARD_VALUE)
                continue;

            this.addHealth((EntityPlayer) event.getEntityLiving(), healthItem.getHealthAmount());
            break;
        }
    }

    @SubscribeEvent
    public void itemUse(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.isSpectator() || event.getWorld().isRemote)
            return;

        ItemStack stack = player.getHeldItem(event.getHand());
        if (stack.getItemUseAction() == EnumAction.EAT || player.canEat(false)) {
            return;
        }

        if (stack.getItemUseAction() == EnumAction.DRINK) {
            return;
        }

        if (!SKHealthConfig.enabled) {
            return;
        }

        for (SKHealthConfig.HealthItem healthItem : SKHealthConfig.health_items) {

            if (healthItem.getItem() != stack.getItem()) {
                continue;
            }

            if (healthItem.getMeta() != stack.getMetadata() && healthItem.getMeta() != OreDictionary.WILDCARD_VALUE)
                continue;

            if (this.addHealth(player, healthItem.getHealthAmount())) {
                stack.shrink(1);
                return;
            }
            break;
        }
    }

    private void setHealth(EntityPlayer player, Operation type) {
        IHealth health = SKCapabilities.getCapability(player);
        int baseHealth = (int) this.getAttribute(player).getBaseValue();

        health.setHealthEnabled(true);
        switch (type) {
            case STARTING:
                health.setStartingHealth(SKHealthConfig.starting_health);
                this.setAdditionalHealth(player, SKHealthConfig.starting_health - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case MAX:
                health.setMaxHealth(SKHealthConfig.max_health);
                this.setAdditionalHealth(player, SKHealthConfig.max_health - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case MIN:
                health.setMinHealth(SKHealthConfig.min_health);
                this.setAdditionalHealth(player, SKHealthConfig.min_health - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case THRESHOLD:
                this.setAdditionalHealth(player, health.getCurrentThreshold() - baseHealth);
                setHealth(player, Operation.SAVE);
                break;
            case REMOVE:
                health.setMaxHealth(SKHealthConfig.max_health);
                health.setMinHealth(SKHealthConfig.min_health);
                int min_health = Math.max(SKHealthConfig.min_health, Math.abs(health.getCurrentThreshold()));
                int removedHealth = getCurrentHealth(player) - SKHealthConfig.health_decrease;
                int addedHealth = Math.max(removedHealth, min_health) - baseHealth;
                this.setAdditionalHealth(player, addedHealth);
                setHealth(player, Operation.SAVE);
                break;
            case SAVE:
                health.setMaxHealth(SKHealthConfig.max_health);
                health.setMinHealth(SKHealthConfig.min_health);
                health.setStartingHealth(SKHealthConfig.starting_health);
                break;
        }
    }

    private boolean addHealth(EntityPlayer player, int amount) {
        IHealth health = SKCapabilities.getCapability(player);
        int baseHealth = (int) this.getAttribute(player).getBaseValue();

        if (health.getAdditionalHealth() >= SKHealthConfig.max_health - baseHealth) {
            return false;
        }

        if (health.getAdditionalHealth() + amount > SKHealthConfig.max_health - baseHealth) {
            amount = SKHealthConfig.max_health - baseHealth - health.getAdditionalHealth();
        }

        health.setAdditionalHealth(health.getAdditionalHealth() + amount);
        this.setAdditionalHealth(player, health.getAdditionalHealth());
        this.setHealth(player, Operation.SAVE);
        if (!this.updateThreshold(player)) {
            this.playerMessage(player, "health.statskeeper.item_add", amount);
        }
        return true;
    }

    private void setAdditionalHealth(EntityPlayer player, int value) {
        IHealth health = SKCapabilities.getCapability(player);
        this.removeModifier(player);
        health.setAdditionalHealth(value);
        this.addModifier(player, value);
        player.setHealth(player.getMaxHealth());
    }

    private enum Operation {
        STARTING, MAX, MIN, THRESHOLD, REMOVE, SAVE
    }

    private boolean hasConfigChanged(EntityPlayer player) {
        IHealth health = SKCapabilities.getCapability(player);

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

    private int getCurrentHealth(EntityPlayer player) {
        IHealth health = SKCapabilities.getCapability(player);
        int baseHealth = (int) this.getAttribute(player).getBaseValue();
        return health.getAdditionalHealth() + baseHealth;
    }

    private boolean updateThreshold(EntityPlayer player) {
        IHealth health = SKCapabilities.getCapability(player);

        if (SKHealthConfig.health_thresholds.length == 0) {
            health.setCurrentThreshold(0);
            return false;
        }

        List<Integer> thresholdList = Arrays.stream(SKHealthConfig.health_thresholds).boxed().collect(Collectors.toList());

        int oldThreshold = health.getCurrentThreshold();
        if (health.getCurrentThreshold() != 0 && !thresholdList.contains(health.getCurrentThreshold())) {
            Stream<Integer> stream = thresholdList.stream().filter(i -> Math.abs(i) <= this.getCurrentHealth(player));
            int value = stream.reduce((first, second) -> second).orElse(thresholdList.get(0));
            health.setCurrentThreshold(value);
        }

        for (int i : SKHealthConfig.health_thresholds) {

            if (health.getCurrentThreshold() == 0 && i < 0) {
                health.setCurrentThreshold(i);
                break;
            }

            if (Math.abs(health.getCurrentThreshold()) < Math.abs(i) && this.getCurrentHealth(player) >= Math.abs(i)) {
                health.setCurrentThreshold(i);
            }
        }

        if (oldThreshold != health.getCurrentThreshold() && (oldThreshold != 0 || thresholdList.get(0) > 0)) {
            this.playerMessage(player, "health.statskeeper.threshold", Math.abs(health.getCurrentThreshold()));
            return true;
        }
        return false;
    }

    private void clearHealthData(EntityPlayer player) {
        IHealth health = SKCapabilities.getCapability(player);
        health.setAdditionalHealth(0);
        health.setStartingHealth(0);
        health.setCurrentThreshold(0);
        health.setMinHealth(0);
        health.setMaxHealth(0);
        health.setHealthEnabled(false);
    }

    private IAttributeInstance getAttribute(EntityPlayer player) {
        return player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
    }

    private void addModifier(EntityPlayer player, int amount) {
        IAttributeInstance attribute = this.getAttribute(player);
        attribute.applyModifier(new AttributeModifier(STATS_KEEPER_HEALTH_UUID, StatsKeeper.MOD_ID, amount, 0));
    }

    private void removeModifier(EntityPlayer player) {
        AttributeModifier modifier = this.getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID);
        if (modifier != null) {
            this.getAttribute(player).removeModifier(modifier);
        }
    }

    private boolean hasModifier(EntityPlayer player) {
        return this.getAttribute(player).getModifier(STATS_KEEPER_HEALTH_UUID) != null;
    }

    private void playerMessage(EntityPlayer player, String key, double health) {
        if (health == 0) return;
        double messageAmount = health / 2.0;
        TextComponentTranslation component = messageAmount % 1 != 0 ? new TextComponentTranslation(key, messageAmount) : new TextComponentTranslation(key, (int) messageAmount);
        player.sendStatusMessage(component, true);
    }
}
