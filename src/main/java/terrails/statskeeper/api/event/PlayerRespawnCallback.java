package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerRespawnCallback {

    public static final Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class,
            (listeners) -> (player, isEnd) -> {
                for (PlayerRespawnCallback event : listeners) {
                    event.onPlayerRespawn(player, isEnd);
                }
            }
    );

    void onPlayerRespawn(PlayerEntity player, boolean isEnd);
}
