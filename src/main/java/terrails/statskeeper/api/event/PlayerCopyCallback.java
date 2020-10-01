package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerCopyCallback {

    Event<PlayerCopyCallback> EVENT = EventFactory.createArrayBacked(PlayerCopyCallback.class,
            (listeners) -> (player, oldPlayer, isEnd) -> {
                for (PlayerCopyCallback event : listeners) {
                    event.onPlayerCopy(player, oldPlayer, isEnd);
                }
            }
    );

    void onPlayerCopy(ServerPlayerEntity player, ServerPlayerEntity oldPlayer, boolean isEnd);
}
