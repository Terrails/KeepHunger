package terrails.statskeeper.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.statskeeper.api.event.PlayerRespawnCallback;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onClientStatus",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "net/minecraft/server/PlayerManager.respawnPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void onPlayerRespawnEnd(CallbackInfo info) {
        PlayerRespawnCallback.EVENT.invoker().onPlayerRespawn(this.player, true);
    }

    @Inject(method = "onClientStatus",
            at = @At(value = "INVOKE_ASSIGN",
                    target = "net/minecraft/server/PlayerManager.respawnPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;",
                    ordinal = 1, shift = At.Shift.AFTER))
    private void onPlayerRespawn(CallbackInfo info) {
        PlayerRespawnCallback.EVENT.invoker().onPlayerRespawn(this.player, false);
    }
}
