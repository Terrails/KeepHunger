package terrails.statskeeper.mixin;

import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.statskeeper.event.PlayerEvent;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onClientStatus",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/PlayerManager;method_14556(Lnet/minecraft/server/network/ServerPlayerEntity;" +
                    "Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;", ordinal = 0, shift = At.Shift.AFTER))
    private void onPlayerRespawnEnd(CallbackInfo info) {
        HandlerArray<PlayerEvent.Respawn> handler = PlayerEvent.PLAYER_RESPAWN;

        for (PlayerEvent.Respawn event : handler.getBackingArray()) {
            event.onPlayerRespawn(this.player, true);
        }
    }

    @Inject(method = "onClientStatus",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/PlayerManager;method_14556(Lnet/minecraft/server/network/ServerPlayerEntity;" +
                    "Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;", ordinal = 1, shift = At.Shift.AFTER))
    private void onPlayerRespawn(CallbackInfo info) {
        HandlerArray<PlayerEvent.Respawn> handler = PlayerEvent.PLAYER_RESPAWN;

        for (PlayerEvent.Respawn event : handler.getBackingArray()) {
            event.onPlayerRespawn(this.player, false);
        }
    }
}
