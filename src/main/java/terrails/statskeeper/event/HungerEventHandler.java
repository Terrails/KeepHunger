package terrails.statskeeper.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import terrails.statskeeper.api.SKPotions;
import terrails.statskeeper.config.configs.SKHungerConfig;

public class HungerEventHandler {

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {

            if (SKHungerConfig.KEEP_HUNGER.get()) {
                int value = Math.max(SKHungerConfig.LOWEST_HUNGER.get(), event.getOriginal().getFoodStats().getFoodLevel());
                event.getEntityPlayer().getFoodStats().setFoodLevel(value);
            }

            if (SKHungerConfig.KEEP_SATURATION.get()) {

                if (SKHungerConfig.KEEP_SATURATION_WITH_HUNGER.get() && event.getOriginal().getFoodStats().needFood()) {
                    return;
                }

                float value = Math.max(SKHungerConfig.LOWEST_SATURATION.get(), event.getOriginal().getFoodStats().getSaturationLevel());

                FoodStats foodStats = event.getEntityPlayer().getFoodStats();
                ObfuscationReflectionHelper.setPrivateValue(FoodStats.class, foodStats, value, "field_75125_b");
            }
        }
    }

    @SubscribeEvent
    public void applyEffect(PlayerRespawnEvent event) {
        EntityPlayer player = event.getPlayer();
        if (SKHungerConfig.NO_APPETITE_TIME.get() > 0 && !player.isCreative()) {
            player.addPotionEffect(new PotionEffect(SKPotions.NO_APPETITE, SKHungerConfig.NO_APPETITE_TIME.get() * 20, 0, false, false, true));
        }
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        EnumAction action = event.getItemStack().getUseAction();
        if (SKHungerConfig.NO_APPETITE_TIME.get() > 0 && (action == EnumAction.EAT || action == EnumAction.DRINK) && event.getEntityPlayer().isPotionActive(SKPotions.NO_APPETITE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (SKHungerConfig.NO_APPETITE_TIME.get() > 0 && event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CAKE && event.getEntityPlayer().isPotionActive(SKPotions.NO_APPETITE)) {
            event.setCanceled(true);
        }
    }
}
