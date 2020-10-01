package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface PlayerUseItemCallback {

    Event<PlayerUseItemCallback> EVENT = EventFactory.createArrayBacked(PlayerUseItemCallback.class,
            (listeners) -> (player, world, hand) -> {
                for (PlayerUseItemCallback event : listeners) {
                    event.onItemUse(player, world, hand);
                }
            }
    );

    void onItemUse(ServerPlayerEntity player, World world, Hand hand);
}
