package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerCloneCallback {

    public static final Event<PlayerCloneCallback> EVENT = EventFactory.createArrayBacked(PlayerCloneCallback.class,
            (listeners) -> (player, oldPlayer, isEnd) -> {
                for (PlayerCloneCallback event : listeners) {
                    event.onPlayerClone(player, oldPlayer, isEnd);
                }
            }
    );

    void onPlayerClone(PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd);
}
