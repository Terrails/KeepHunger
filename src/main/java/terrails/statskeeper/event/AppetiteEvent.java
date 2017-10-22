package terrails.statskeeper.event;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.potion.ModPotions;
import terrails.statskeeper.potion.NoAppetiteEffect;

@Mod.EventBusSubscriber
public class AppetiteEvent {

    @SubscribeEvent
    public static void applyEffect(PlayerEvent.PlayerRespawnEvent event) {
        if (ConfigHandler.noEating && !event.player.isCreative()) {
            event.player.addPotionEffect(new PotionEffect(ModPotions.APPETITE, ConfigHandler.noEatingTime * 20));
        }
    }

    @SubscribeEvent
    public static void itemInteract(PlayerInteractEvent.RightClickItem event) {
        if (ConfigHandler.noEating && event.getItemStack().getItemUseAction() == EnumAction.EAT && event.getEntityPlayer().isPotionActive(ModPotions.APPETITE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void blockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (ConfigHandler.noEating && event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CAKE && event.getEntityPlayer().isPotionActive(ModPotions.APPETITE)) {
            event.setCanceled(true);
        }
    }
}