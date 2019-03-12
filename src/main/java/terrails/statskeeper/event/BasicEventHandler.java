package terrails.statskeeper.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import terrails.statskeeper.config.SKConfig;

public class BasicEventHandler {

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && SKConfig.KEEP_EXPERIENCE.get()) {
            EntityPlayer player = event.getEntityPlayer();
            boolean checkGameRule = player.getEntityWorld().getGameRules().getBoolean("keepInventory");
            if (!checkGameRule) {
                player.giveExperiencePoints(event.getOriginal().experienceTotal);
            }
        }
    }

    @SubscribeEvent
    public void dropExperience(LivingExperienceDropEvent event) {
        if (!SKConfig.DROP_EXPERIENCE.get() && event.getEntity() instanceof EntityPlayer) {
            event.setCanceled(true);
        }
    }
}
