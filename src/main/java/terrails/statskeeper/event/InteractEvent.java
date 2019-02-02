package terrails.statskeeper.event;

import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class InteractEvent {

    public static HandlerArray<UseFinished> PLAYER_USE_FINISHED = new HandlerArray<>(UseFinished.class);

    @FunctionalInterface
    public interface UseFinished {
        void onItemUseFinished(PlayerEntity player, ItemStack stack);
    }
}
