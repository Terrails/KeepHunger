package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerCopyCallback {

    Event<PlayerCopyCallback> EVENT = EventFactory.createArrayBacked(PlayerCopyCallback.class,
            (listeners) -> (player, oldPlayer, isEnd) -> {
                for (PlayerCopyCallback event : listeners) {
                    event.onPlayerCopy(player, oldPlayer, isEnd);
                }
            }
    );

    void onPlayerCopy(PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd);
}
