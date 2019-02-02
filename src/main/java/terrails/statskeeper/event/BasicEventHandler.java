package terrails.statskeeper.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.statskeeper.config.SKConfig;

public class BasicEventHandler {

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && SKConfig.keep_experience) {
            EntityPlayer player = event.getEntityPlayer();
            boolean checkGameRule = player.getEntityWorld().getGameRules().getBoolean("keepInventory");
            if (SKConfig.keep_experience && !checkGameRule) {
                player.addExperience(event.getOriginal().experienceLevel);
            }
        }
    }

    @SubscribeEvent
    public void dropExperience(LivingExperienceDropEvent event) {
        if (!SKConfig.drop_experience && event.getEntity() instanceof EntityPlayer) {
            event.setCanceled(true);
        }
    }
}
