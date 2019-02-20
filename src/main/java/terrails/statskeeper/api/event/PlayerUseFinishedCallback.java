package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface PlayerUseFinishedCallback {

    public static final Event<PlayerUseFinishedCallback> EVENT = EventFactory.createArrayBacked(PlayerUseFinishedCallback.class,
            (listeners) -> (player, stack) -> {
                for (PlayerUseFinishedCallback event : listeners) {
                    event.onItemUseFinished(player, stack);
                }
            }
    );

    void onItemUseFinished(PlayerEntity player, ItemStack stack);
}
