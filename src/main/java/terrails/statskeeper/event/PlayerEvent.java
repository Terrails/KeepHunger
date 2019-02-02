package terrails.statskeeper.event;

import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerEvent {

    public static HandlerArray<Clone> PLAYER_CLONE = new HandlerArray<>(PlayerEvent.Clone.class);
    public static HandlerArray<Respawn> PLAYER_RESPAWN = new HandlerArray<>(PlayerEvent.Respawn.class);
    public static HandlerArray<Join> PLAYER_JOIN = new HandlerArray<>(PlayerEvent.Join.class);

    @FunctionalInterface
    public interface Clone {
        void onPlayerClone(PlayerEntity player, PlayerEntity oldPlayer, boolean isEnd);
    }

    @FunctionalInterface
    public interface Respawn {
        void onPlayerRespawn(PlayerEntity player, boolean isEnd);
    }

    @FunctionalInterface
    public interface Join {
        void onPlayerJoin(PlayerEntity player);
    }
}
