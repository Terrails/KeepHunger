package terrails.statskeeper.handler;

import net.minecraft.entity.player.PlayerEntity;
import terrails.statskeeper.config.SKConfig;
import terrails.statskeeper.api.event.PlayerCloneCallback;

public class BasicHandler {

    public static PlayerCloneCallback playerCloneEvent = (PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd) -> {
        if (!isEnd) {
            SKConfig config = SKConfig.instance;
            boolean checkGameRule = player.getEntityWorld().getGameRules().getBoolean("keepInventory");
            if (config.keep_experience && !checkGameRule) {
                player.addExperience(oldPlayer.experienceLevel);
            }
        }
    };
}
