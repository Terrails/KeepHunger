package terrails.keephunger.event;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.keephunger.config.ConfigHandler;
import terrails.keephunger.potion.ModPotions;

public class AppetiteEvent {
    //Appetite Effect
    @SubscribeEvent
    public void itemInteract(PlayerInteractEvent.RightClickItem event)
    {
        if ((ConfigHandler.noEating) &&
                (event.getItemStack().getItemUseAction() == EnumAction.EAT) &&
                (event.getEntityPlayer().isPotionActive(ModPotions.appetite))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void milkInteract(PlayerInteractEvent.RightClickItem event)
    {
        if ((ConfigHandler.noEating) &&
                (event.getItemStack().getItem() == Items.MILK_BUCKET) &&
                (event.getEntityPlayer().isPotionActive(ModPotions.appetite))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void blockInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if ((ConfigHandler.noEating) &&
                (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CAKE) &&
                (event.getEntityPlayer().isPotionActive(ModPotions.appetite))) {
            event.setCanceled(true);
        }
    }
}
