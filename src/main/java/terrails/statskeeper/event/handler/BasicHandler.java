package terrails.statskeeper.event.handler;

import net.minecraft.entity.player.PlayerEntity;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.event.PlayerEvent;

public class BasicHandler {

    public static PlayerEvent.Clone playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd) {
            SKConfig config = SKConfig.instance;
            boolean checkGameRule = player.getEntityWorld().getGameRules().getBoolean("keepInventory");
            if (config.keep_experience && !checkGameRule) {
                player.addExperience(oldPlayer.experienceLevel);
            }
        }
    };
}
