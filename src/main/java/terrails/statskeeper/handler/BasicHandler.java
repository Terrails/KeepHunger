package terrails.statskeeper.handler;

import net.minecraft.entity.player.PlayerEntity;
import terrails.statskeeper.api.event.PlayerCloneCallback;
import terrails.statskeeper.config.SKConfig;

public class BasicHandler {

    public static PlayerCloneCallback playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd) {
            boolean checkGameRule = player.getEntityWorld().getGameRules().getBoolean("keepInventory");
            if (SKConfig.keep_experience && !checkGameRule) {
                player.addExperience(oldPlayer.experienceLevel);
            }
        }
    };
}
