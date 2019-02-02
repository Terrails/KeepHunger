package terrails.statskeeper.event;

import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import terrails.statskeeper.api.SKPotions;
import terrails.statskeeper.config.SKConfig;

import java.lang.reflect.Field;

public class HungerEventHandler {

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {

            if (SKConfig.Hunger.keep_hunger) {
                int hungerLevel = event.getOriginal().getFoodStats().getFoodLevel();
                if (SKConfig.Hunger.lowest_hunger > hungerLevel) {
                    hungerLevel = SKConfig.Hunger.lowest_hunger;
                }
                event.getEntityPlayer().getFoodStats().setFoodLevel(hungerLevel);
            }

            if (SKConfig.Hunger.keep_saturation) {
                FoodStats foodStats = event.getEntityPlayer().getFoodStats();
                float saturationLevel = event.getOriginal().getFoodStats().getSaturationLevel();
                if (SKConfig.Hunger.lowest_saturation > saturationLevel) {
                    saturationLevel = SKConfig.Hunger.lowest_saturation;
                }

                try {
                    Field setSaturationLevel = ObfuscationReflectionHelper.findField(FoodStats.class, "field_75125_b");
                    setSaturationLevel.set(foodStats, saturationLevel);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SubscribeEvent
    public void applyEffect(PlayerRespawnEvent event) {
        if (SKConfig.Hunger.no_appetite_time > 0 && !event.player.isCreative()) {
            event.player.addPotionEffect(new PotionEffect(SKPotions.APPETITE, SKConfig.Hunger.no_appetite_time * 20, 0, false, false));
        }
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        EnumAction action = event.getItemStack().getItemUseAction();
        if (SKConfig.Hunger.no_appetite_time > 0 && (action == EnumAction.EAT || action == EnumAction.DRINK) && event.getEntityPlayer().isPotionActive(SKPotions.APPETITE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (SKConfig.Hunger.no_appetite_time > 0 && event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CAKE && event.getEntityPlayer().isPotionActive(SKPotions.APPETITE)) {
            event.setCanceled(true);
        }
    }
}
