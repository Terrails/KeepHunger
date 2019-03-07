package terrails.statskeeper.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.statskeeper.config.SKConfig;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        SKConfig.initialize();
    }
}
