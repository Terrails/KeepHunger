package terrails.statskeeper.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import terrails.statskeeper.api.SKPotions;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.config.SKHungerConfig;

public class BasicStatHandler {

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            EntityPlayer player = event.getEntityPlayer();
            EntityPlayer oldPlayer = event.getOriginal();

            boolean keepInventory = player.getEntityWorld().getGameRules().getBoolean("keepInventory");
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
        EntityPlayer player = event.getPlayer();
        if (SKHungerConfig.NO_APPETITE_TIME > 0 && !player.isCreative()) {
            player.addPotionEffect(new PotionEffect(SKPotions.NO_APPETITE, SKHungerConfig.NO_APPETITE_TIME * 20, 0, false, false, true));
        }
    }

    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event) {
        if (!event.getEntityPlayer().isPotionActive(SKPotions.NO_APPETITE)) {
            return;
        }

        EnumAction action = event.getItemStack().getUseAction();
        if (action == EnumAction.DRINK || action == EnumAction.EAT) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityPlayer().isPotionActive(SKPotions.NO_APPETITE)) {
            return;
        }

        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (block instanceof BlockCake) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void experienceDrop(LivingExperienceDropEvent event) {
        if (!SKConfig.DROP_EXPERIENCE && event.getEntity() instanceof EntityPlayer) {
            event.setCanceled(true);
        }
    }
}
