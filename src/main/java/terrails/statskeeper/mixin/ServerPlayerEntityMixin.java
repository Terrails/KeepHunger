package terrails.statskeeper.mixin;

import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrails.statskeeper.StatsKeeper;
import terrails.statskeeper.api.data.health.IHealth;
import terrails.statskeeper.api.data.health.IHealthManager;
import terrails.statskeeper.api.event.PlayerCloneCallback;
import terrails.statskeeper.config.SKHealthConfig;
import terrails.statskeeper.handler.health.HealthManager;

import java.util.Collection;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IHealthManager {

    @Inject(method = "method_14203(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At("RETURN"))
    private void onPlayerClone(ServerPlayerEntity oldPlayer, boolean isEnd, CallbackInfo callbackInfo) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerCloneCallback.EVENT.invoker().onPlayerClone(player, oldPlayer, isEnd);
    }

    @Inject(method = "changeDimension", at = @At("RETURN"))
    private void changeDimension(DimensionType dimensionType, CallbackInfoReturnable<Entity> info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        EntityAttributeContainer attributeContainer = (EntityAttributeContainer) player.getAttributeContainer();
        Collection<EntityAttributeInstance> attributeInstances = attributeContainer.buildTrackedAttributesCollection();
        if (!attributeInstances.isEmpty()) player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getEntityId(), attributeInstances));
    }

    private IHealth health_handler = new HealthManager();

    @Override
    public IHealth getHealthHandler() {
        return this.health_handler;
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void readCustomDataFromTag(CompoundTag tag, CallbackInfo info) {
        if (tag.containsKey(StatsKeeper.MOD_ID)) {
            tag = tag.getCompound(StatsKeeper.MOD_ID);
        }

        if (tag.containsKey("sk:is_enabled")) {
            this.health_handler.setHealthEnabled(tag.getBoolean("sk:is_enabled"));
        }

        if (tag.containsKey("sk:starting_health")) {
            this.health_handler.setStartingHealth(tag.getInt("sk:starting_health"));
        }

        if (tag.containsKey("sk:additional_health")) {
            this.health_handler.setAdditionalHealth(tag.getInt("sk:additional_health"));
        }

        if (tag.containsKey("sk:max_health")) {
            this.health_handler.setMaxHealth(tag.getInt("sk:max_health"));
        }

        if (tag.containsKey("sk:min_health")) {
            this.health_handler.setMinHealth(tag.getInt("sk:min_health"));
        }

        if (tag.containsKey("sk:health_threshold")) {
            this.health_handler.setCurrentThreshold(tag.getInt("sk:health_threshold"));
        }

        // Compatibility for older versions
        if (tag.containsKey("sk:is_min_start")) {
            boolean min_start = tag.getBoolean("sk:is_min_start");
            this.health_handler.setStartingHealth(min_start ? SKHealthConfig.min_health : SKHealthConfig.max_health);
        }

    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeCustomDataToTag(CompoundTag compound, CallbackInfo info) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("sk:is_enabled", this.health_handler.isHealthEnabled());
        tag.putInt("sk:additional_health", this.health_handler.getAdditionalHealth());
        tag.putInt("sk:max_health", this.health_handler.getMaxHealth());
        tag.putInt("sk:min_health", this.health_handler.getMinHealth());
        tag.putInt("sk:starting_health", this.health_handler.getStartingHealth());
        tag.putInt("sk:health_threshold", this.health_handler.getCurrentThreshold());
        compound.put(StatsKeeper.MOD_ID, tag);
    }
}