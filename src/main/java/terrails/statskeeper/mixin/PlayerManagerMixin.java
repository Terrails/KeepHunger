package terrails.statskeeper.mixin;

import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.client.network.packet.EntityAttributesClientPacket;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.statskeeper.event.PlayerEvent;

import java.util.Collection;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        HandlerArray<PlayerEvent.Join> handler = PlayerEvent.PLAYER_JOIN;

        for (PlayerEvent.Join event : handler.getBackingArray()) {
            event.onPlayerJoin(player);
        }
    }

    @Inject(method = "method_14598", at = @At("RETURN"))
    private void onPlayerDimensionChange(ServerPlayerEntity player, DimensionType dimensionType, CallbackInfo info) {
        EntityAttributeContainer attributeContainer = (EntityAttributeContainer) player.getAttributeContainer();
        Collection<EntityAttributeInstance> attributeInstances = attributeContainer.method_6213();
        if (!attributeInstances.isEmpty()) player.networkHandler.sendPacket(new EntityAttributesClientPacket(player.getEntityId(), attributeInstances));
    }
}
