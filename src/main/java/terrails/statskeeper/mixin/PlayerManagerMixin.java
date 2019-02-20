package terrails.statskeeper.mixin;

import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
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
import terrails.statskeeper.api.event.PlayerJoinCallback;

import java.util.Collection;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        PlayerJoinCallback.EVENT.invoker().onPlayerJoin(player);
    }

    @Inject(method = "method_14598", at = @At("RETURN"))
    private void onPlayerDimensionChange(ServerPlayerEntity player, DimensionType dimensionType, CallbackInfo info) {
        EntityAttributeContainer attributeContainer = (EntityAttributeContainer) player.getAttributeContainer();
        Collection<EntityAttributeInstance> attributeInstances = attributeContainer.method_6213();
        if (!attributeInstances.isEmpty()) player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getEntityId(), attributeInstances));
    }
}
