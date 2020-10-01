package terrails.statskeeper.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerDropExperienceCallback {

    Event<PlayerDropExperienceCallback> EVENT = EventFactory.createArrayBacked(PlayerDropExperienceCallback.class,
            (listeners) -> (player) -> {
                boolean ret = true;
                for (PlayerDropExperienceCallback event : listeners) {
                    if (!event.dropExperience(player) & ret) {
                        ret = false;
                    }
                }
                return ret;
            }
    );

    boolean dropExperience(PlayerEntity player);
}
