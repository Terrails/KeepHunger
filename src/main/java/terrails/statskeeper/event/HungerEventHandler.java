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
import terrails.statskeeper.config.configs.SKHungerConfig;

public class HungerEventHandler {

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {

            if (SKHungerConfig.keep_hunger) {
                int value = Math.max(SKHungerConfig.lowest_hunger, event.getOriginal().getFoodStats().getFoodLevel());
                event.getEntityPlayer().getFoodStats().setFoodLevel(value);
            }

            if (SKHungerConfig.keep_saturation) {
                float value = Math.max(SKHungerConfig.lowest_saturation, event.getOriginal().getFoodStats().getSaturationLevel());
                FoodStats foodStats = event.getEntityPlayer().getFoodStats();
                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, foodStats, value, "field_75125_b");
            }
        }
    }

    @SubscribeEvent
    public void applyEffect(PlayerRespawnEvent event) {
        if (SKHungerConfig.no_appetite_time > 0 && !event.player.isCreative()) {
            event.player.addPotionEffect(new PotionEffect(SKPotions.APPETITE, SKHungerConfig.no_appetite_time * 20, 0, false, false));
        }
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        EnumAction action = event.getItemStack().getItemUseAction();
        if (SKHungerConfig.no_appetite_time > 0 && (action == EnumAction.EAT || action == EnumAction.DRINK) && event.getEntityPlayer().isPotionActive(SKPotions.APPETITE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (SKHungerConfig.no_appetite_time > 0 && event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CAKE && event.getEntityPlayer().isPotionActive(SKPotions.APPETITE)) {
            event.setCanceled(true);
        }
    }
}
