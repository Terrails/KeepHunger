package terrails.statskeeper.event;

import net.minecraft.block.Block;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.FoodStats;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import terrails.statskeeper.api.SKEffects;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.config.SKHungerConfig;

public class BasicStatHandler {

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PlayerEntity player = event.getEntityPlayer();
            PlayerEntity oldPlayer = event.getOriginal();

            boolean keepInventory = player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
            if (SKConfig.KEEP_EXPERIENCE && !keepInventory) {
                player.addExperienceLevel(oldPlayer.experienceLevel);
            }

            if (SKHungerConfig.KEEP_HUNGER) {
                int value = Math.max(SKHungerConfig.LOWEST_HUNGER, oldPlayer.getFoodStats().getFoodLevel());
                player.getFoodStats().setFoodLevel(value);
            }

            if (SKHungerConfig.KEEP_SATURATION) {
                if (SKHungerConfig.KEEP_SATURATION_WITH_HUNGER && oldPlayer.getFoodStats().needFood()) {
                    return;
                }

                float value = Math.max(SKHungerConfig.LOWEST_SATURATION, oldPlayer.getFoodStats().getSaturationLevel());
                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, player.getFoodStats(), value, "field_75125_b");
            }
        }
    }

    @SubscribeEvent
    public void playerRespawn(PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (SKHungerConfig.NO_APPETITE_TIME > 0 && !player.isCreative()) {
            player.addPotionEffect(new EffectInstance(SKEffects.NO_APPETITE, SKHungerConfig.NO_APPETITE_TIME * 20, 0, false, false, true));
        }
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        if (!event.getEntityPlayer().isPotionActive(SKEffects.NO_APPETITE)) {
            return;
        }

        Food food = event.getEntityPlayer().getHeldItemMainhand().getItem().getFood();
        if (food != null && event.getEntityPlayer().canEat(food.canEatWhenFull())) {
            event.setCanceled(true);
            return;
        }

        food = event.getEntityPlayer().getHeldItemOffhand().getItem().getFood();
        if (food != null && event.getEntityPlayer().canEat(food.canEatWhenFull())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityPlayer().isPotionActive(SKEffects.NO_APPETITE)) {
            return;
        }

        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (block instanceof CakeBlock) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void experienceDrop(LivingExperienceDropEvent event) {
        if (!SKConfig.DROP_EXPERIENCE && event.getEntity() instanceof PlayerEntity) {
            event.setCanceled(true);
        }
    }
}
