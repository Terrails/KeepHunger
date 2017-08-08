package terrails.statskeeper.event;

import com.pam.harvestcraft.blocks.blocks.BlockPamCake;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.config.ConfigHandler;
import terrails.statskeeper.potion.ModPotions;

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
    public void blockInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if ((ConfigHandler.noEating) &&
                (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CAKE) &&
                (event.getEntityPlayer().isPotionActive(ModPotions.appetite))) {
            event.setCanceled(true);
        }
    }

    @Optional.Method(modid = "harvestcraft")
    @SubscribeEvent
    public void blockPamInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if ((ConfigHandler.noEating) &&
                (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockPamCake) &&
                (event.getEntityPlayer().isPotionActive(ModPotions.appetite))) {
            event.setCanceled(true);
        }
    }

}
