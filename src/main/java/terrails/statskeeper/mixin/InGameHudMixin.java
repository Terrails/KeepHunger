package terrails.statskeeper.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow private @Final MinecraftClient client;

    /**
     * Makes sure to the texture turns to vanilla on each loop iteration,
     * and then when needed manually into a custom one with {@link StatusEffect#hasIcon()}
     */

    @Inject(method = "method_1765", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void fixStatusEffectRenderer(CallbackInfo info) {
        this.client.getTextureManager().bindTexture(ContainerScreen.BACKGROUND_TEXTURE);
    }
}
