package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerRespawnCallback {

    Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class,
            (listeners) -> (player, isEnd) -> {
                for (PlayerRespawnCallback event : listeners) {
                    event.onPlayerRespawn(player, isEnd);
                }
            }
    );

    void onPlayerRespawn(ServerPlayerEntity player, boolean isEnd);
}
