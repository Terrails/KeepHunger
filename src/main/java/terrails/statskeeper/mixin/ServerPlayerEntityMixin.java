package terrails.statskeeper.mixin;

import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrails.statskeeper.api.data.HealthManager;
import terrails.statskeeper.api.event.PlayerCopyCallback;
import terrails.statskeeper.config.SKHealthConfig;
import terrails.statskeeper.health.PlayerHealthManager;

import java.util.Collections;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements HealthManager.Accessor {

    private HealthManager healthManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        this.healthManager = SKHealthConfig.enabled ? new PlayerHealthManager(player) : null;
    }

    @Override
    public Optional<HealthManager> getHealthManager() {
        return Optional.ofNullable(this.healthManager);
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void readCustomDataFromTag(CompoundTag tag, CallbackInfo info) {
        if (this.healthManager != null) {
            this.healthManager.deserialize(tag);
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeCustomDataToTag(CompoundTag tag, CallbackInfo info) {
        if (this.healthManager != null) {
            this.healthManager.serialize(tag);
        }
    }

    @Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At("RETURN"))
    private void onPlayerCopy(ServerPlayerEntity oldPlayer, boolean isEnd, CallbackInfo callbackInfo) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerCopyCallback.EVENT.invoker().onPlayerCopy(player, oldPlayer, isEnd);
    }

    @Inject(method = "changeDimension", at = @At("RETURN"))
    private void changeDimension(DimensionType dimensionType, CallbackInfoReturnable<Entity> info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        Packet packet = new EntityAttributesS2CPacket(player.getEntityId(), Collections.singleton(attribute));
        player.networkHandler.sendPacket(packet);
    }
}