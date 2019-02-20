package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerJoinCallback {

    public static final Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class,
            (listeners) -> (player) -> {
                for (PlayerJoinCallback event : listeners) {
                    event.onPlayerJoin(player);
                }
            }
    );

    void onPlayerJoin(PlayerEntity player);
}
