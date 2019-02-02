package terrails.statskeeper.mixin;

import net.fabricmc.fabric.util.HandlerArray;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.statskeeper.api.IPlayerHealth;
import terrails.statskeeper.event.PlayerEvent;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IPlayerHealth {

    @Inject(method = "method_14203(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At("RETURN"))
    private void onPlayerClone(ServerPlayerEntity oldPlayer, boolean isEnd, CallbackInfo callbackInfo) {
        HandlerArray<PlayerEvent.Clone> handler = PlayerEvent.PLAYER_CLONE;
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        for (PlayerEvent.Clone event : handler.getBackingArray()) {
            event.onPlayerClone(player, oldPlayer, isEnd);
        }
    }

    private int additional_health = 0;
    private boolean is_health_enabled = false;
    private int max_health = 0;
    private int min_health = 0;

    @Override
    public boolean hasSKAdditionalHealth() {
        return additional_health > 0;
    }

    @Override
    public void setSKAdditionalHealth(int health) {
        this.additional_health = health;
    }

    @Override
    public int getSKAdditionalHealth() {
        return this.additional_health;
    }

    @Override
    public boolean isSKHealthEnabled() {
        return this.is_health_enabled;
    }

    @Override
    public void setSKHealthEnabled(boolean val) {
        this.is_health_enabled = val;
    }

    @Override
    public int getSKMaxHealth() {
        return this.max_health;
    }

    @Override
    public void setSKMaxHealth(int health) {
        this.max_health = health;
    }

    @Override
    public int getSKMinHealth() {
        return this.min_health;
    }

    @Override
    public void setSKMinHealth(int health) {
        this.min_health = health;
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void readCustomDataFromTag(CompoundTag tag, CallbackInfo info) {
        if (tag.containsKey("sk:is_enabled")) {
            this.setSKHealthEnabled(tag.getBoolean("sk:is_enabled"));
        }

        if (tag.containsKey("sk:additional_health")) {
            this.setSKAdditionalHealth(tag.getInt("sk:additional_health"));
        }

        if (tag.containsKey("sk:max_health")) {
            this.setSKMaxHealth(tag.getInt("sk:max_health"));
        }

        if (tag.containsKey("sk:min_health")) {
            this.setSKMinHealth(tag.getInt("sk:min_health"));
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void writeCustomDataToTag(CompoundTag tag, CallbackInfo info) {
        tag.putBoolean("sk:is_enabled", this.isSKHealthEnabled());
        tag.putInt("sk:additional_health", this.getSKAdditionalHealth());
        tag.putInt("sk:max_health", this.getSKMaxHealth());
        tag.putInt("sk:min_health", this.getSKMinHealth());
    }
}