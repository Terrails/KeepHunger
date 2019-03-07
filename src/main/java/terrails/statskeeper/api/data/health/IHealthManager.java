package terrails.statskeeper.api.data.health;

import net.minecraft.entity.player.PlayerEntity;

public interface IHealthManager {

    static IHealth getInstance(PlayerEntity player) {
        return ((IHealthManager) player).getHealthHandler();
    }

    IHealth getHealthHandler();
}
